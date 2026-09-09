// org/foss/lens/ui/screens/ConfirmAssetViewModel.kt
package org.foss.lens.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.foss.lens.data.AssetLocal
import org.foss.lens.data.sync.AssetSyncer
import org.foss.lens.domain.Asset
import org.foss.lens.domain.AssetContract
import org.foss.lens.domain.AssetRepository
import org.foss.lens.domain.Components
import org.foss.lens.domain.SyncState
import org.foss.lens.observability.AppLogger
import org.foss.lens.ui.PendingScanHolder

/**
 * Confirma/edita el activo recién escaneado. Nada se envía sin pasar por aquí:
 * el usuario puede corregir campos o descartar el registro por completo.
 */
class ConfirmAssetViewModel(
    private val repo: AssetRepository,
    private val syncer: AssetSyncer,
    holder: PendingScanHolder
) : ViewModel() {

    data class UiState(
        val asset: Asset? = null,
        val saving: Boolean = false,
        val saved: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        _state.value = UiState(asset = holder.consume())
    }

    fun update(transform: (Asset) -> Asset) {
        _state.value = _state.value.let { s ->
            s.copy(asset = s.asset?.let(transform))
        }
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
    fun setTipo(value: String) = update { it.copy(tipo = value) }
    fun setEstado(value: String) = update { it.copy(estado = value) }

    suspend fun save() {
        val current = _state.value.asset ?: return
        if (current.serial.isBlank()) {
            _state.value = _state.value.copy(error = "El serial es obligatorio.")
            return
        }
        _state.value = _state.value.copy(saving = true, error = null)
        try {
            val stored = repo.upsert(
                current.copy(syncState = SyncState.PENDING, serverId = null, errorMsg = null)
            )
            syncer.flushPending()
            _state.value = _state.value.copy(
                saving = false,
                saved = true,
                asset = stored.withSync(SyncState.PENDING)
            )
        } catch (e: Exception) {
            AppLogger.error("ConfirmVM", "No se pudo guardar el activo", e)
            _state.value = _state.value.copy(
                saving = false,
                error = "No se pudo guardar: ${e.message}"
            )
        }
    }

    companion object {
        val tipos = AssetContract.tipos
        val estados = AssetContract.estados
    }
}

internal fun Asset.blankFor(serial: String): Asset =
    Asset(serial = serial, componentes = Components())
