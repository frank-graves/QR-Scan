// app/src/main/java/org/foss/lens/presentation/HistoryVM.kt
package org.foss.lens.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.foss.lens.domain.Archive
import org.foss.lens.domain.Codex

class HistoryVM(private val archive: Archive) : ViewModel() {
    private val _entries = MutableStateFlow<List<Codex>>(emptyList())
    val entries: StateFlow<List<Codex>> = _entries.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _entries.value = archive.all()
        }
    }

    fun clear() {
        viewModelScope.launch {
            archive.clear()
            load()
        }
    }
}
