// org/foss/lens/ui/screens/ManualAssetViewModel.kt
package org.foss.lens.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.foss.lens.data.sync.AssetSyncer
import org.foss.lens.domain.Asset
import org.foss.lens.domain.AssetRepository
import org.foss.lens.domain.Origen
import org.foss.lens.domain.SyncState
import org.foss.lens.observability.AppLogger

/**
 * Registro manual (F4): sirve cuando el QR no existe o está dañado. El usuario
 * teclea los datos; el guardado es la confirmación, así que no pasa por el holder.
 */
class ManualAssetViewModel(
    private val repo: AssetRepository,
    private val syncer: AssetSyncer
) : ViewModel() {

    data class UiState(
        val asset: Asset = Asset(serial = ""),
        val saving: Boolean = false,
        val saved: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun update(transform: (Asset) -> Asset) {
        _state.value = _state.value.let { s -> s.copy(asset = transform(s.asset)) }
    }

    fun updateSerial(value: String) = update { it.copy(serial = value.trim()) }
    fun updateMarca(value: String) = update { it.copy(marca = value.ifBlank { null }) }
    fun updateModelo(value: String) = update { it.copy(modelo = value.ifBlank { null }) }
    fun updateUbicacion(value: String) = update { it.copy(ubicacion = value.ifBlank { null }) }
    fun updateCpu(value: String) = update {
        it.copy(componentes = it.componentes.copy(cpu = value.ifBlank { null }))
    }
    fun updateSo(value: String) = update {
        it.copy(componentes = it.componentes.copy(so = value.ifBlank { null }))
    }
    fun updateRam(value: String) = update {
        it.copy(componentes = it.componentes.copy(ramGb = value.toIntOrNull()))
    }
    fun updateDisco(value: String) = update {
        it.copy(componentes = it.componentes.copy(discoGb = value.toIntOrNull()))
    }
    fun updateNotas(value: String) = update { it.copy(notas = value.ifBlank { null }) }

    suspend fun save() {
        val current = _state.value.asset
        if (current.serial.isBlank()) {
            _state.value = _state.value.copy(error = "El serial es obligatorio.")
            return
        }
        _state.value = _state.value.copy(saving = true, error = null)
        try {
            repo.upsert(
                current.copy(origen = Origen.MANUAL, syncState = SyncState.PENDING, serverId = null)
            )
            syncer.flushPending()
            _state.value = _state.value.copy(saving = false, saved = true)
        } catch (e: Exception) {
            AppLogger.error("ManualVM", "No se pudo guardar el activo", e)
            _state.value = _state.value.copy(
                saving = false,
                error = "No se pudo guardar: ${e.message}"
            )
        }
    }
}
