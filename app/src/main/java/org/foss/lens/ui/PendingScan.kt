// org/foss/lens/ui/PendingScan.kt
package org.foss.lens.ui

import androidx.compose.runtime.Immutable
import org.foss.lens.domain.Asset
import org.foss.lens.domain.vehicle.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Puente entre el ScannerViewModel y las pantallas de destino: el escáner deja
 * aquí el activo recién parseado (o la ficha del taller con los campos que
 * traía el QR) y la pantalla de destino lo consume. Mantenerlo fuera de la
 * navegación evita pasar JSON gigantes por rutas.
 */
@Immutable
class PendingScanHolder {
    private val _asset = MutableStateFlow<Asset?>(null)
    val asset: StateFlow<Asset?> = _asset.asStateFlow()

    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle: StateFlow<Vehicle?> = _vehicle.asStateFlow()

    fun offer(asset: Asset) {
        _asset.value = asset
    }

    fun consume(): Asset? {
        val current = _asset.value
        _asset.value = null
        return current
    }

    /** El escáner del taller deja la ficha (placa + opcionales decodificados del QR). */
    fun offerVehicle(vehicle: Vehicle) {
        _vehicle.value = vehicle
    }

    fun consumeVehicle(): Vehicle? {
        val current = _vehicle.value
        _vehicle.value = null
        return current
    }
}

/** Eventos de una sola vez que el escáner emite hacia la navegación. */
sealed class ScannerEvent {
    /** Hay un activo nuevo esperando confirmación en el holder. */
    data class OpenConfirm(val serial: String) : ScannerEvent()

    /** El serial escaneado ya existe localmente. */
    data class OpenExisting(val serial: String) : ScannerEvent()

    /** El QR era del taller: la ficha ya está en el holder esperando al tab Taller. */
    data class OpenGarage(val plate: String) : ScannerEvent()

    /** Contenido genérico guardado en el historial. */
    data class Notice(val message: String) : ScannerEvent()
}
