// org/foss/lens/remote/AssetSyncGateway.kt
package org.foss.lens.remote

import org.foss.lens.domain.Asset

/**
 * Contrato del backend. Hoy hay una implementación contra mockAPI; si el
 * destino cambia (API real, Sheets vía Apps Script…), solo cambia esta pieza.
 */
interface AssetSyncGateway {
    /** False cuando no hay endpoint configurado (BuildConfig vacío). */
    val isConfigured: Boolean

    /** Envía el activo y devuelve su id en el servidor. */
    suspend fun push(asset: Asset): String
}
