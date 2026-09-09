// org/foss/lens/data/ScanHistoryLocal.kt
package org.foss.lens.data

import org.foss.lens.data.local.ScanHistoryDao
import org.foss.lens.data.local.ScanHistoryEntity
import org.foss.lens.domain.Codex
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Historial de escaneos genéricos (todo lo que no resultó ser un activo).
 * El inventario de PCs vive aparte, en AssetLocal.
 */
class ScanHistoryLocal(private val dao: ScanHistoryDao) {

    suspend fun record(payload: String, format: String, timestamp: Instant = Instant.now()) {
        dao.insert(ScanHistoryEntity(payload = payload, format = format, timestamp = timestamp))
    }

    fun observeRecent(): Flow<List<Codex>> =
        dao.observeRecent().map { rows -> rows.map { Codex(it.id, it.payload, it.format, it.timestamp) } }

    suspend fun clear() = dao.clear()
}
