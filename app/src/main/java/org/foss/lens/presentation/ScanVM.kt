// app/src/main/java/org/foss/lens/presentation/ScanVM.kt
package org.foss.lens.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.foss.lens.domain.Archive
import org.foss.lens.domain.ScanState
import org.foss.lens.infrastructure.Lens

class ScanVM(
    private val lens: Lens,
    private val archive: Archive
) : ViewModel() {
    private val _state = MutableStateFlow<ScanState>(ScanState.Idle)
    val state: StateFlow<ScanState> = _state.asStateFlow()
    private var scanningJob: Job? = null

    fun startScanning() {
        if (scanningJob?.isActive == true) return
        scanningJob = viewModelScope.launch {
            lens.start().collect { scanState ->
                _state.value = scanState
                if (scanState is ScanState.Success) {
                    archive.save(scanState.codex)
                }
            }
        }
    }

    fun stopScanning() {
        scanningJob?.cancel()
        scanningJob = null
        lens.stop()
    }

    override fun onCleared() {
        stopScanning()
        super.onCleared()
    }
}
