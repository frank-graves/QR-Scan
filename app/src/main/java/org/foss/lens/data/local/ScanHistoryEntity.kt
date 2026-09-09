// org/foss/lens/data/local/ScanHistoryEntity.kt
package org.foss.lens.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Escaneo genérico (no-PC) para el historial. Los activos de inventario NO
 * viven aquí: tienen su propia tabla `assets`.
 */
@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val payload: String,
    val format: String,
    val timestamp: Instant
)
