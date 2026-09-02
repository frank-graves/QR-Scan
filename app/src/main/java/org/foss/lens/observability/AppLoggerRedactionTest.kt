// app/src/test/java/org/foss/lens/observability/AppLoggerRedactionTest.kt
package org.foss.lens.observability

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLoggerRedactionTest {
    @Test
    fun redactaPayloadTokenSecret() {
        assertEquals(
            "payload=[REDACTED] token=[REDACTED] secret=[REDACTED]",
            AppLogger.redactPii("payload=abc123 token=xyz secret=1234")
        )
    }

    @Test
    fun noTocaTextoNormal() {
        assertEquals("status idle", AppLogger.redactPii("status idle"))
    }
}