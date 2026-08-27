// org/foss/lens/data/ArchiveLocal.kt
package org.foss.lens.data

import org.foss.lens.domain.Archive
import org.foss.lens.domain.Codex
import org.foss.lens.data.local.ArchiveDatabase
import org.foss.lens.data.local.ArchiveEntity

class ArchiveLocal(private val db: ArchiveDatabase) : Archive {
    override suspend fun save(entry: Codex): Long {
        val entity = ArchiveEntity.fromDomain(entry)
        return db.archiveDao().insert(entity)
    }

    override suspend fun all(): List<Codex> {
        return db.archiveDao().getAll().map { it.toDomain() }
    }

    override suspend fun delete(id: Long) {
        db.archiveDao().delete(id)
    }

    override suspend fun clear() {
        db.archiveDao().clear()
    }
}