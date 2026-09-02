// app/src/test/java/org/foss/lens/infrastructure/CodexDecoderRotationTest.kt
package org.foss.lens.infrastructure

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class CodexDecoderRotationTest {
    @Test
    fun rotate90Clockwise() {
        val input = byteArrayOf(0, 1, 2, 3, 4, 5) // 2x3
        val expected = byteArrayOf(4, 2, 0, 5, 3, 1)
        assertArrayEquals(expected, CodexDecoder.rotateYPlane(input, 2, 3, 90))
    }
}