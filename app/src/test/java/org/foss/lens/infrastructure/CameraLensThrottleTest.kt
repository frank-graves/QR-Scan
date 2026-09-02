package org.foss.lens.infrastructure

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraLensThrottleTest {
    @Test
    fun withinCooldown_discardsFrame() {
        assertTrue(CameraLens.isWithinCooldown(now = 600L, last = 200L, cooldown = 500L))
    }

    @Test
    fun outsideCooldown_allowsAnalysis() {
        assertFalse(CameraLens.isWithinCooldown(now = 800L, last = 200L, cooldown = 500L))
    }

    @Test
    fun exactBoundary_disallowsAnalysis() {
        assertTrue(CameraLens.isWithinCooldown(now = 700L, last = 200L, cooldown = 500L))
    }
}
