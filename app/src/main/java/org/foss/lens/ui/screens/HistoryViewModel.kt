// org/foss/lens/ui/screens/HistoryViewModel.kt
package org.foss.lens.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.foss.lens.data.ScanHistoryLocal
import org.foss.lens.domain.Codex

/** Historial de escaneos genéricos (los que no son activos de inventario). */
class HistoryViewModel(private val history: ScanHistoryLocal) : ViewModel() {

    val entries: StateFlow<List<Codex>> = history.observeRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun clearAll() {
        viewModelScope.launch { history.clear() }
    }
}
