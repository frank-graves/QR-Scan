// org/foss/lens/ui/PendingScan.kt
package org.foss.lens.ui

import org.foss.lens.domain.Asset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Puente entre el ScannerViewModel y la pantalla de confirmación: el escáner
 * deja aquí el activo recién parseado y ConfirmScreen lo consume. Mantenerlo
 * fuera de la navegación evita pasar JSON gigantes por rutas.
 */
class PendingScanHolder {
    private val _asset = MutableStateFlow<Asset?>(null)
    val asset: StateFlow<Asset?> = _asset.asStateFlow()

    fun offer(asset: Asset) {
        _asset.value = asset
    }

    fun consume(): Asset? {
        val current = _asset.value
        _asset.value = null
        return current
    }
}

/** Eventos de una sola vez que el escáner emite hacia la navegación. */
sealed class ScannerEvent {
    /** Hay un activo nuevo esperando confirmación en el holder. */
    data class OpenConfirm(val serial: String) : ScannerEvent()

    /** El serial escaneado ya existe localmente. */
    data class OpenExisting(val serial: String) : ScannerEvent()

    /** Contenido genérico guardado en el historial. */
    data class Notice(val message: String) : ScannerEvent()
}
