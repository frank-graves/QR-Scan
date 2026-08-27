// org/foss/lens/infrastructure/Lens.kt
package org.foss.lens.infrastructure

import org.foss.lens.domain.ScanState
import kotlinx.coroutines.flow.Flow

interface Lens {
    fun start(): Flow<ScanState>
    fun stop()
    suspend fun requestPermissions(): Boolean
}