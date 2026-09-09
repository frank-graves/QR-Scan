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
import org.foss.lens.ui.PendingScanHolder
import org.foss.lens.ui.ScannerEvent

/**
 * Decide qué hacer con cada código leído. El escáner entrega bytes; aquí se
 * clasifican. Duplicados consecutivos del mismo QR se ignoran durante 2,5 s
 * para no abrir la pantalla de confirmación 30 veces con el mismo código.
 */
class ScannerViewModel(
    private val classifier: AssetClassifier,
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

        val asset = classifier.parse(codex.payload)
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

        history.record(payload = codex.payload, format = codex.format, timestamp = codex.timestamp)
        return ScannerEvent.Notice("Contenido guardado en el historial")
    }
}
