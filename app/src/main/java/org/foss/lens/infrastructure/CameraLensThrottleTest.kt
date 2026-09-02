// app/src/test/java/org/foss/lens/infrastructure/CameraLensThrottleTest.kt
package org.foss.lens.infrastructure

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraLensThrottleTest {
    @Test
    fun dentroDelCooldown_descartaFrame() {
        assertTrue(CameraLens.isWithinCooldown(now = 600L, last = 200L, cooldown = 500L))
    }

    @Test
    fun fueraDelCooldown_permiteAnalisis() {
        assertFalse(CameraLens.isWithinCooldown(now = 800L, last = 200L, cooldown = 500L))
    }

    @Test
    fun bordeExacto_noPermite() {
        assertTrue(CameraLens.isWithinCooldown(now = 700L, last = 200L, cooldown = 500L))
    }
}