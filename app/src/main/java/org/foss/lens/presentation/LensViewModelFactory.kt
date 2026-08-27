// org/foss/lens/presentation/LensViewModelFactory.kt
package org.foss.lens.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.foss.lens.domain.Archive
import org.foss.lens.infrastructure.Lens

class LensViewModelFactory(
    private val archive: Archive,
    private val lens: Lens
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ScanVM::class.java) ->
                ScanVM(lens, archive) as T
            modelClass.isAssignableFrom(HistoryVM::class.java) ->
                HistoryVM(archive) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}