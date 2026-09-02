package org.foss.lens.observability

object GoldenSignals {
    fun coldStart(ms: Long) = AppLogger.recordMetric("cold_start_ms", ms.toDouble())
    fun frameRender(ms: Long) = AppLogger.recordMetric("frame_render_ms", ms.toDouble())
    fun analyzerOk() = AppLogger.recordMetric("analyzer_ok_rate", 1.0)
    fun analyzerError() = AppLogger.recordMetric("analyzer_error_rate", 1.0)
    fun saturation(memKb: Int, batteryPct: Int) {
        AppLogger.recordMetric("saturation_mem_kb", memKb.toDouble())
        AppLogger.recordMetric("saturation_battery_pct", batteryPct.toDouble())
    }
}
