// org/foss/lens/domain/Archive.kt
package org.foss.lens.domain

interface Archive {
    suspend fun save(entry: Codex): Long
    suspend fun all(): List<Codex>
    suspend fun delete(id: Long)
    suspend fun clear()
}