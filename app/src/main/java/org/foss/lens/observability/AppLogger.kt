package org.foss.lens.observability

import android.content.Context
import android.util.Log
import org.foss.lens.BuildConfig

object AppLogger {
    @Volatile private var db: ObservabilityDb? = null

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
     * Redacts only key=value / key: value shaped text. Free-form prose
     * containing a raw payload is NOT caught — that's a contract, not a
     * bug: callers must never log raw payloads outside the key=value form.
     */
    fun redactPii(raw: String): String =
        raw.replace(
            Regex("(?i)(payload|qr|token|secret|api[_-]?key)\\s*[:=]\\s*([^\\s,;]+)"),
            "$1=[REDACTED]"
        )
}
