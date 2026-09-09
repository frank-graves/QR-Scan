// org/foss/lens/domain/Asset.kt
package org.foss.lens.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Origen de un registro: escaneado de un QR o tecleado a mano. */
@Serializable
enum class Origen {
    @SerialName("qr")
    QR,

    @SerialName("manual")
    MANUAL
}

/**
 * Componentes de la PC dentro del QR. Todo opcional: un código puede traer
 * solo el serial y el resto se completa a mano en la pantalla de confirmación.
 */
@Serializable
data class Components(
    val cpu: String? = null,
    @SerialName("ramGb") val ramGb: Int? = null,
    @SerialName("discoGb") val discoGb: Int? = null,
    val so: String? = null
)

/** Estado de sincronización con el backend (mockAPI). */
enum class SyncState { PENDING, SYNCED, FAILED }

/**
 * Un activo (PC) tal y como lo ve el dominio.
 *
 * `localId`/`syncState`/`serverId` son de la capa de datos; viven aquí para
 * que la UI no necesite un tipo espejo: el repositorio mapea a la entidad Room.
 */
data class Asset(
    val serial: String,
    val marca: String? = null,
    val modelo: String? = null,
    val tipo: String? = null,
    val estado: String? = null,
    val ubicacion: String? = null,
    val componentes: Components = Components(),
    val notas: String? = null,
    val origen: Origen = Origen.QR,
    val schema: String = AssetContract.SCHEMA,
    val localId: Long = 0L,
    val syncState: SyncState = SyncState.PENDING,
    val serverId: String? = null,
    val errorMsg: String? = null,
    val createdLocalMs: Long = 0L
) {
    fun withLocalId(id: Long, createdMs: Long): Asset =
        copy(localId = id, createdLocalMs = createdMs)

    fun withSync(state: SyncState, server: String? = serverId, error: String? = errorMsg): Asset =
        copy(syncState = state, serverId = server, errorMsg = error)
}

/** Contrato del QR de inventario. Versionar aquí evita romper lecturas viejas. */
object AssetContract {
    const val SCHEMA = "lens.asset.v1"
    const val SCHEMA_KEY = "schema"
    const val SERIAL_KEY = "serial"

    val estados = listOf("operativo", "en_reparacion", "baja", "pendiente")
    val tipos = listOf("desktop", "laptop", "monitor", "otro")
}
