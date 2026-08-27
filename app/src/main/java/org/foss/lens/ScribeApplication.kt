// org/foss/lens/ScribeApplication.kt
package org.foss.lens

import android.app.Application
import org.foss.lens.data.ArchiveLocal
import org.foss.lens.data.local.ArchiveDatabase
import org.foss.lens.domain.Archive
import org.foss.lens.infrastructure.CameraLens
import org.foss.lens.infrastructure.CodexDecoder
import org.foss.lens.infrastructure.Lens

class ScribeApplication : Application() {
    private val db by lazy { ArchiveDatabase.getInstance(this) }
    val archive: Archive by lazy { ArchiveLocal(db) }
    val decoder: CodexDecoder by lazy { CodexDecoder() }

    fun createLens(lifecycleOwner: androidx.lifecycle.LifecycleOwner): Lens {
        return CameraLens(this, lifecycleOwner, decoder)
    }

    companion object {
        lateinit var instance: ScribeApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}