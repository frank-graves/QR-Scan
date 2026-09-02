package org.foss.lens.infrastructure

import org.foss.lens.domain.ScanState
import kotlinx.coroutines.flow.Flow

/**
 * Camera abstraction.
 *
 * The Activity talks to this seam, never to CameraX directly, so the
 * scanning pipeline stays testable without hardware.
 */
interface Lens {
    fun start(): Flow<ScanState>
    fun stop()
    suspend
