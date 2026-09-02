package org.foss.lens.presentation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.foss.lens.domain.Codex
import java.time.Instant

class HistoryAdapterDiffTest {
    private val diff = HistoryAdapter.DiffCallback

    @Test
    fun sameId_sameItem() {
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = Instant.ofEpochMilli(1000))
        val b = Codex(id = 1L, payload = "y", format = "QR", timestamp = Instant.ofEpochMilli(2000))
        assertTrue(diff.areItemsTheSame(a, b))
    }

    @Test
    fun differentId_differentItem() {
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = Instant.ofEpochMilli(1000))
        val b = Codex(id = 2L, payload = "x", format = "QR", timestamp = Instant.ofEpochMilli(1000))
        assertFalse(diff.areItemsTheSame(a, b))
    }

    @Test
    fun sameContent_sameContent() {
        val t = Instant.ofEpochMilli(1000)
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = t)
        val b = Codex(id = 1L, payload = "x", format = "QR", timestamp = t)
        assertTrue(diff.areContentsTheSame(a, b))
    }

    @Test
    fun differentPayload_differentContent() {
        val t = Instant.ofEpochMilli(1000)
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = t)
        val b = Codex(id = 1L, payload = "z", format = "QR", timestamp = t)
        assertFalse(diff.areContentsTheSame(a, b))
    }
}
