// app/src/test/java/org/foss/lens/presentation/HistoryAdapterDiffTest.kt
package org.foss.lens.presentation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.foss.lens.domain.Codex
import java.time.Instant

class HistoryAdapterDiffTest {
    private val diff = HistoryAdapter.DiffCallback

    @Test
    fun mismoId_mismoItem() {
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = Instant.ofEpochMilli(1000))
        val b = Codex(id = 1L, payload = "y", format = "QR", timestamp = Instant.ofEpochMilli(2000))
        assertTrue(diff.areItemsTheSame(a, b))
    }

    @Test
    fun distintoId_itemDistinto() {
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = Instant.ofEpochMilli(1000))
        val b = Codex(id = 2L, payload = "x", format = "QR", timestamp = Instant.ofEpochMilli(1000))
        assertFalse(diff.areItemsTheSame(a, b))
    }

    @Test
    fun mismoContenido_mismoContenido() {
        val t = Instant.ofEpochMilli(1000)
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = t)
        val b = Codex(id = 1L, payload = "x", format = "QR", timestamp = t)
        assertTrue(diff.areContentsTheSame(a, b))
    }

    @Test
    fun distintoPayload_contenidoDistinto() {
        val t = Instant.ofEpochMilli(1000)
        val a = Codex(id = 1L, payload = "x", format = "QR", timestamp = t)
        val b = Codex(id = 1L, payload = "z", format = "QR", timestamp = t)
        assertFalse(diff.areContentsTheSame(a, b))
    }
}