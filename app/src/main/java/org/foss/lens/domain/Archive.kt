package org.foss.lens.domain

/**
 * Persistence seam for scanned payloads.
 *
 * Implementations are expected to be suspend-friendly and local-only:
 * the archive is a disposable history, never synced off-device.
 */
interface Archive {
    suspend fun save(entry: Codex): Long
    suspend fun all(): List<Codex>
    suspend fun delete(id: Long)
    suspend fun clear()
}
