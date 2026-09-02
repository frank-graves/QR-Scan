// app/src/main/java/org/foss/lens/observability/CrashHandler.kt
package org.foss.lens.observability

object CrashHandler {
    fun install() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            AppLogger.error("CrashHandler", "Uncaught exception on ${t.name}", e)
            previous?.uncaughtException(t, e)
        }
    }
}