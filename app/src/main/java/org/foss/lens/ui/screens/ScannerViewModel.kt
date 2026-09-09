// org/foss/lens/ui/screens/ScannerViewModel.kt
package org.foss.lens.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.foss.lens.data.ScanHistoryLocal
import org.foss.lens.domain.AssetClassifier
import org.foss.lens.domain.AssetRepository
import org.foss.lens.domain.Codex
import org.foss.lens.domain.vehicle.VehicleCodec
import org.foss.lens.domain.vehicle.cleanQrPayload
import org.foss.lens.observability.AppLogger
import org.foss.lens.ui.PendingScanHolder
import org.foss.lens.ui.ScannerEvent

/** Tag único para rastrear en logcat por qué un QR con forma de JSON no llegó a su destino. */
private const val SCANNER_ERROR = "SCANNER_ERROR"

/**
 * Decide qué hacer con cada código leído. El escáner entrega bytes; aquí se
 * clasifican. Duplicados consecutivos del mismo QR se ignoran durante 2,5 s
 * para no abrir la pantalla de confirmación 30 veces con el mismo código.
 */
class ScannerViewModel(
    private val classifier: AssetClassifier,
    private val vehicleCodec: VehicleCodec,
    private val history: ScanHistoryLocal,
    private val assets: AssetRepository,
    private val pending: PendingScanHolder
) : ViewModel() {

    data class UiState(
        val statusText: String = "Esperando un código…",
        val busy: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var lastKey: String? = null
    private var lastAtMs: Long = 0L

    /** Procesa un código ya decodificado y devuelve la acción de navegación, si la hay. */
    suspend fun onCodex(codex: Codex): ScannerEvent? {
        val now = System.currentTimeMillis()
        val key = codex.payload.take(80)
        if (key == lastKey && now - lastAtMs < 2_500) return null
        lastKey = key
        lastAtMs = now

        // Un solo payload limpio gobierna todo el ruteo: si el generador del QR
        // embelleció el JSON con comillas curvas o NBSP, aquí se vuelve
        // parseable. El historial, eso sí, guarda el texto crudo escaneado.
        val cleanPayload = codex.payload.cleanQrPayload()

        // El taller primero: un QR de vehículo no debe caer al historial como
        // "contenido genérico" ni intentar clasificarse como PC de inventario.
        val vehicle = try {
            vehicleCodec.decode(cleanPayload)
        } catch (boom: Exception) {
            // El codec hoy responde null antes que lanzar, pero si algo escapa
            // (o el decodificador cambia mañana) este log muestra la causa
            // exacta antes de que el QR caiga al historial como texto suelto.
            AppLogger.error(SCANNER_ERROR, "Error parseando QR a Vehicle", boom)
            null
        }
        if (vehicle != null) {
            _state.value = UiState(statusText = "Vehículo del taller: ${vehicle.plate}", busy = true)
            pending.offerVehicle(vehicle)
            return ScannerEvent.OpenGarage(vehicle.plate)
        }

        val asset = classifier.parse(cleanPayload)
        if (asset != null) {
            _state.value = UiState(statusText = "Activo detectado: ${asset.serial}", busy = true)
            val existing = assets.findBySerial(asset.serial)
            return if (existing != null) {
                ScannerEvent.OpenExisting(existing.serial)
            } else {
                pending.offer(asset)
                ScannerEvent.OpenConfirm(asset.serial)
            }
        }

        // Nadie lo reconoció. Si además tiene forma de objeto JSON no es una
        // placa pelada ni basura casual: es un JSON huérfano (schema ajeno o
        // malformado) y conviene dejar rastro antes de archivarlo, o el
        // historial se traga el motivo en silencio.
        if (cleanPayload.startsWith("{") && cleanPayload.endsWith("}")) {
            AppLogger.warn(SCANNER_ERROR, "JSON sin dueño (ni taller ni inventario): ${cleanPayload.take(200)}")
        }

        history.record(payload = codex.payload, format = codex.format, timestamp = codex.timestamp)
        return ScannerEvent.Notice("Contenido guardado en el historial")
    }
}
