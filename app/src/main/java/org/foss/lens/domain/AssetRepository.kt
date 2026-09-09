// org/foss/lens/domain/AssetRepository.kt
package org.foss.lens.domain

import kotlinx.coroutines.flow.Flow

/**
 * Puerta de entrada al almacén local de activos (inventario de PCs).
 *
 * La implementación Room se encarga de la clave única por `serial` y de
 * persistir el estado de sincronización; el resto de la app habla con esto.
 */
interface AssetRepository {

    /** Todos los activos, del más reciente al más viejo. */
    fun observeAll(): Flow<List<Asset>>

    /** Activo por serial (para detectar "ya registrada"). */
    suspend fun findBySerial(serial: String): Asset?

    /**
     * Inserta o reemplaza (misma clave = serial). Devuelve el activo persistido
     * con su id local y marca de tiempo.
     */
    suspend fun upsert(asset: Asset): Asset

    /** Activos pendientes de enviar al backend. */
    suspend fun pending(): List<Asset>

    /** Actualiza el estado de sincronización de un activo. */
    suspend fun mark(
        localId: Long,
        state: SyncState,
        serverId: String? = null,
        error: String? = null
    )
}
