package org.foss.lens.observability

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLoggerRedactionTest {
    @Test
    fun redactsPayloadTokenSecret() {
        assertEquals(
            "payload=[REDACTED] token=[REDACTED] secret=[REDACTED]",
            AppLogger.redactPii("payload=abc123 token=xyz secret=1234")
        )
    }

    @Test
    fun leavesNormalTextAlone() {
        assertEquals("status idle", AppLogger.redactPii("status idle"))
    }

    @Test
    fun redactsColonSeparator() {
        assertEquals(
            "token=[REDACTED]",
            AppLogger.redactPii("token: abc123")
        )
    }
}
