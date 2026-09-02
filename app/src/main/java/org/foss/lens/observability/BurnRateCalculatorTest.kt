// app/src/test/java/org/foss/lens/observability/BurnRateCalculatorTest.kt
package org.foss.lens.observability

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BurnRateCalculatorTest {
    @Test
    fun sinErrores_noCritico() {
        val calc = BurnRateCalculator()
        assertFalse(calc.isCritical(0L))
    }

    @Test
    fun erroresSoloCortoPlazo_marcanCritico() {
        val calc = BurnRateCalculator()
        val now = 60 * 60_000L
        repeat(20) { calc.record(now - 10_000L + it * 100L, error = true) }
        assertTrue(calc.isCritical(now))
    }

    @Test
    fun erroresDistribuidos_noCritico() {
        val calc = BurnRateCalculator()
        val now = 60 * 60_000L
        for (i in 0 until 60) calc.record(now - i * 60_000L, error = true)
        assertFalse(calc.isCritical(now))
    }
}