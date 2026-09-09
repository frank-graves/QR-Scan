// org/foss/lens/ui/screens/InventoryViewModel.kt
package org.foss.lens.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.foss.lens.data.sync.AssetSyncer
import org.foss.lens.domain.Asset
import org.foss.lens.domain.AssetRepository
import org.foss.lens.observability.AppLogger

/** Lista viva del inventario local + disparo manual de sincronización (F7/F8). */
class InventoryViewModel(
    private val repo: AssetRepository,
    private val syncer: AssetSyncer
) : ViewModel() {

    val items: StateFlow<List<Asset>> = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    private val _lastSyncMessage = MutableStateFlow<String?>(null)
    val lastSyncMessage: StateFlow<String?> = _lastSyncMessage.asStateFlow()

    fun syncNow() {
        if (_syncing.value) return
        viewModelScope.launch {
            _syncing.value = true
            _lastSyncMessage.value = null
            try {
                val sent = syncer.flushPending()
                _lastSyncMessage.value = if (sent > 0) {
                    "Enviados $sent activo(s)"
                } else {
                    "Sin pendientes por enviar"
                }
            } catch (e: Exception) {
                AppLogger.error("InventoryVM", "Sync falló", e)
                _lastSyncMessage.value = "Sync falló: ${e.message}"
            } finally {
                _syncing.value = false
            }
        }
    }
}
