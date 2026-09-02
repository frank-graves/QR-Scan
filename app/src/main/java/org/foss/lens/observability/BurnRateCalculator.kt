// app/src/main/java/org/foss/lens/observability/BurnRateCalculator.kt
package org.foss.lens.observability

class BurnRateCalculator(
    private val shortWindowMs: Long = 5 * 60_000L,
    private val longWindowMs: Long = 60 * 60_000L,
    private val criticalFactor: Double = 14.4
) {
    private data class Sample(val ts: Long, val error: Boolean)
    private val samples = ArrayDeque<Sample>()
    private val lock = Any()

    fun record(nowMs: Long, error: Boolean) {
        synchronized(lock) {
            samples.addLast(Sample(nowMs, error))
            while (samples.isNotEmpty() && samples.first().ts < nowMs - longWindowMs) {
                samples.removeFirst()
            }
        }
    }

    fun burnRatio(nowMs: Long): Double {
        val (shortRate, longRate) = synchronized(lock) {
            val shortRate = samples.count { it.ts >= nowMs - shortWindowMs && it.error } / (shortWindowMs / 60_000.0)
            val longRate = samples.count { it.ts >= nowMs - longWindowMs && it.error } / (longWindowMs / 60_000.0)
            shortRate to longRate
        }
        return when {
            longRate > 0.0 -> shortRate / longRate
            shortRate > 0.0 -> Double.MAX_VALUE
            else -> 0.0
        }
    }

    fun isCritical(nowMs: Long): Boolean = burnRatio(nowMs) >= criticalFactor
}