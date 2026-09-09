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

    /**
     * Whether the camera permission is already granted.
     *
     * Part of the seam on purpose: the UI decides *how* to ask (dialog vs
     * settings), but only the lens knows what hardware access it needs.
     */
    suspend fun requestPermissions(): Boolean
}
