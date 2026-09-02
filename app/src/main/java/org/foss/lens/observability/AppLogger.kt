// app/src/main/java/org/foss/lens/observability/AppLogger.kt
package org.foss.lens.observability

import android.content.Context
import android.util.Log
import org.foss.lens.BuildConfig

object AppLogger {
    @Volatile
    private var db: ObservabilityDb? = null

    fun init(context: Context) {
        // Double-check without synchronized allowed two instances if init was
        // called from different threads; today it's a single call in onCreate,
        // but the lock cost is zero and it removes the entire bug class.
        synchronized(this) {
            if (db == null) db = ObservabilityDb(context.applicationContext)
        }
    }

    fun debug(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
            persist(0, tag, msg)
        }
    }

    fun info(tag: String, msg: String) {
        Log.i(tag, msg)
        persist(1, tag, msg)
    }

    fun warn(tag: String, msg: String) {
        Log.w(tag, msg)
        persist(2, tag, msg)
    }

    fun error(tag: String, msg: String, t: Throwable? = null) {
        val full = if (t != null) "$msg\n${Log.getStackTraceString(t)}" else msg
        Log.e(tag, full)
        persist(3, tag, full)
    }

    fun recordMetric(name: String, value: Double) = db?.insertMetric(name, value)

    fun goldenSignals(): Map<String, Double> = db?.getMetrics() ?: emptyMap()

    fun export(): java.io.File? = db?.exportLogs()

    private fun persist(level: Int, tag: String, msg: String) = db?.insertLog(level, tag, redactPii(msg))

    /**
     * Redacts sensitive PII in three passes, then applies a blunt heuristic.
     *
     * Pass 1 kills classic key=value / key: value pairs (covers logs from our own code).
     * Pass 2 neutralizes URL query strings carrying a secret without destroying the URL.
     * Pass 3 scrubs JSON string values associated with sensitive keys.
     *
     * If after all that the remaining text still looks like a JWT (three dot-separated
     * base64url segments) or a JSON blob with suspicious keys, we don't gamble: we return
     * [REDACTED_PAYLOAD]. A partially redacted token is still a token, and we'd rather
     * lose a log line than leak a fragment.
     */
    fun redactPii(raw: String): String {
        var sanitized = raw

        sanitized = sanitized.replace(
            Regex("(?i)\\b(payload|qr|token|secret|api[_-]?key|access[_-]?token|auth)\\s*[:=]\\s*([^\\s&,;\"'<>]+)"),
            "$1=[REDACTED]"
        )

        sanitized = sanitized.replace(
            Regex("(?i)([?&](token|secret|api[_-]?key|access[_-]?token|auth)=)[^&#\\s]+"),
            "$1[REDACTED]"
        )

        sanitized = sanitized.replace(
            Regex("(?i)(\"(payload|qr|token|secret|api[_-]?key|access[_-]?token|auth)\"\\s*:\\s*\")([^\"]*)"),
            "$1[REDACTED]"
        )

        if (Regex("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$").containsMatchIn(sanitized) ||
            Regex("(?i)\"(payload|qr|token|secret|api[_-]?key|access[_-]?token|auth)\"\\s*:").containsMatchIn(sanitized)
        ) {
            return "[REDACTED_PAYLOAD]"
        }

        return sanitized
    }
}
