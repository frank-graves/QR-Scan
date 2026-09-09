// org/foss/lens/data/sync/AssetSyncer.kt
package org.foss.lens.data.sync

import org.foss.lens.domain.AssetRepository
import org.foss.lens.domain.SyncState
import org.foss.lens.observability.AppLogger
import org.foss.lens.remote.AssetSyncGateway

/**
 * Cola offline-first: los activos PENDING se empujan al backend en orden y se
 * marcan SYNCED/FAILED uno a uno, de modo que un fallo no bloquee al resto.
 *
 * Quien dispara (Application al detectar red, la UI al confirmar/reenviar)
 * llama a [flushPending]; esta clase no conoce Android ni red.
 */
class AssetSyncer(
    private val repo: AssetRepository,
    private val gateway: AssetSyncGateway
) {

    /** Devuelve cuántos activos se sincronizaron en esta pasada. */
    suspend fun flushPending(): Int {
        if (!gateway.isConfigured) {
            AppLogger.debug(TAG, "Gateway sin configurar; sync omitido")
            return 0
        }
        var synced = 0
        for (asset in repo.pending()) {
            try {
                val serverId = gateway.push(asset)
                repo.mark(asset.localId, SyncState.SYNCED, serverId = serverId)
                synced++
            } catch (e: Exception) {
                // No se interrumpe la cola: se marca el fallo y se sigue.
                repo.mark(asset.localId, SyncState.FAILED, error = e.message)
                AppLogger.error(TAG, "Fallo al sincronizar ${asset.serial}", e)
            }
        }
        if (synced > 0) AppLogger.debug(TAG, "Sincronizados $synced activos")
        return synced
    }

    private companion object {
        const val TAG = "AssetSyncer"
    }
}
