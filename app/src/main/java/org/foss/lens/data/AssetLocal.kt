// org/foss/lens/data/AssetLocal.kt
package org.foss.lens.data

import org.foss.lens.data.local.AssetDao
import org.foss.lens.data.local.AssetEntity
import org.foss.lens.domain.Asset
import org.foss.lens.domain.AssetRepository
import org.foss.lens.domain.Components
import org.foss.lens.domain.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Inventario local de activos sobre Room. La clave única por `serial` hace el
 * "upsert": reescanear o re-registrar la misma PC la reemplaza, nunca la duplica.
 */
class AssetLocal(private val dao: AssetDao) : AssetRepository {

    override fun observeAll(): Flow<List<Asset>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun findBySerial(serial: String): Asset? =
        dao.bySerial(serial)?.toDomain()

    override suspend fun upsert(asset: Asset): Asset {
        val previous = dao.bySerial(asset.serial)
        val createdMs = previous?.createdLocalMs ?: System.currentTimeMillis()
        val newId = dao.upsert(asset.toEntity(createdMs))
        return asset
            .withLocalId(newId, createdMs)
            .withSync(asset.syncState, asset.serverId, asset.errorMsg)
    }

    override suspend fun pending(): List<Asset> = dao.pending().map { it.toDomain() }

    override suspend fun mark(
        localId: Long,
        state: SyncState,
        serverId: String?,
        error: String?
    ) = dao.markSync(localId, state, serverId, error)

    private fun Asset.toEntity(createdMs: Long) = AssetEntity(
        localId = localId,
        serial = serial,
        schema = schema,
        marca = marca,
        modelo = modelo,
        tipo = tipo,
        estado = estado,
        ubicacion = ubicacion,
        cpu = componentes.cpu,
        ramGb = componentes.ramGb,
        discoGb = componentes.discoGb,
        so = componentes.so,
        notas = notas,
        origen = origen,
        syncState = syncState,
        serverId = serverId,
        errorMsg = errorMsg,
        createdLocalMs = createdMs
    )

    private fun AssetEntity.toDomain() = Asset(
        serial = serial,
        marca = marca,
        modelo = modelo,
        tipo = tipo,
        estado = estado,
        ubicacion = ubicacion,
        componentes = Components(cpu = cpu, ramGb = ramGb, discoGb = discoGb, so = so),
        notas = notas,
        origen = origen,
        schema = schema,
        localId = localId,
        syncState = syncState,
        serverId = serverId,
        errorMsg = errorMsg,
        createdLocalMs = createdLocalMs
    )
}
