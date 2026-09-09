// org/foss/lens/data/local/AssetEntity.kt
package org.foss.lens.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.foss.lens.domain.Origen
import org.foss.lens.domain.SyncState

/**
 * Registro de inventario (PC). La unicidad por `serial` es la que permite
 * detectar "esta PC ya estaba registrada" y reemplazarla sin duplicados locales.
 */
@Entity(
    tableName = "assets",
    indices = [Index(value = ["serial"], unique = true)]
)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serial: String,
    val schema: String,
    val marca: String? = null,
    val modelo: String? = null,
    val tipo: String? = null,
    val estado: String? = null,
    val ubicacion: String? = null,
    val cpu: String? = null,
    val ramGb: Int? = null,
    val discoGb: Int? = null,
    val so: String? = null,
    val notas: String? = null,
    val origen: Origen = Origen.QR,
    val syncState: SyncState = SyncState.PENDING,
    val serverId: String? = null,
    val errorMsg: String? = null,
    val createdLocalMs: Long = 0
)
