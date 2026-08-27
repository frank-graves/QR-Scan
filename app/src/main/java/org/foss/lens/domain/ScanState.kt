// org/foss/lens/domain/ScanState.kt
package org.foss.lens.domain

sealed class ScanState {
    object Idle : ScanState()
    object Detecting : ScanState()
    data class Success(val codex: Codex) : ScanState()
    data class Error(val cause: Throwable, val message: String? = null) : ScanState()
}