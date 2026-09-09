// org/foss/lens/ui/screens/AssetDetailViewModel.kt
package org.foss.lens.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.foss.lens.data.sync.AssetSyncer
import org.foss.lens.domain.Asset
import org.foss.lens.domain.AssetRepository
import org.foss.lens.domain.SyncState
import org.foss.lens.observability.AppLogger
import org.foss.lens.ui.PendingScanHolder

/** Detalle de un activo + acciones de reenvío y edición. */
class AssetDetailViewModel(
    private val repo: AssetRepository,
    private val syncer: AssetSyncer,
    private val holder: PendingScanHolder
) : ViewModel() {

    private val _asset = MutableStateFlow<Asset?>(null)
    val asset: StateFlow<Asset?> = _asset.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _notice = MutableStateFlow<String?>(null)
    val notice: StateFlow<String?> = _notice.asStateFlow()

    fun bind(serial: String) {
        viewModelScope.launch {
            _asset.value = repo.findBySerial(serial)
        }
    }

    /** Devuelve el activo actual al editor (para "Editar"). */
    fun edit(): Asset? = _asset.value?.also { holder.offer(it) }

    fun resend() {
        val current = _asset.value ?: return
        viewModelScope.launch {
            _busy.value = true
            _notice.value = null
            try {
                repo.upsert(
                    current.copy(syncState = SyncState.PENDING, serverId = null, errorMsg = null)
                )
                syncer.flushPending()
                _asset.value = repo.findBySerial(current.serial)
                _notice.value = "Reintento encolado."
            } catch (e: Exception) {
                AppLogger.error("DetailVM", "Reenvío fallido", e)
                _notice.value = "No se pudo reenviar: ${e.message}"
            } finally {
                _busy.value = false
            }
        }
    }
}
