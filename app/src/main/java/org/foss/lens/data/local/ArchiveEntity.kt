// org/foss/lens/data/local/ArchiveEntity.kt
package org.foss.lens.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "archive")
data class ArchiveEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val payload: String,
    val format: String,
    val timestamp: Instant
) {
    fun toDomain(): Codex = Codex(id, payload, format, timestamp)
    companion object {
        fun fromDomain(codex: Codex) = ArchiveEntity(
            id = codex.id,
            payload = codex.payload,
            format = codex.format,
            timestamp = codex.timestamp
        )
    }
}