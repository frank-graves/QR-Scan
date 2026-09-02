// app/src/main/java/org/foss/lens/ScribeApplication.kt (modificado)
package org.foss.lens

import android.app.Application
import android.os.SystemClock
import org.foss.lens.data.ArchiveLocal
import org.foss.lens.data.local.ArchiveDatabase
import org.foss.lens.domain.Archive
import org.foss.lens.infrastructure.CameraLens
import org.foss.lens.infrastructure.CodexDecoder
import org.foss.lens.infrastructure.Lens
import org.foss.lens.observability.AppLogger
import org.foss.lens.observability.CrashHandler
import org.foss.lens.observability.GoldenSignals

class ScribeApplication : Application() {
    private val db by lazy { ArchiveDatabase.getInstance(this) }
    val archive: Archive by lazy { ArchiveLocal(db) }
    val decoder: CodexDecoder by lazy { CodexDecoder() }

    fun createLens(lifecycleOwner: androidx.lifecycle.LifecycleOwner): Lens =
        CameraLens(this, lifecycleOwner, decoder)

    companion object {
        lateinit var instance: ScribeApplication
            private set
        val processStartMs: Long = SystemClock.uptimeMillis()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppLogger.init(this)
        CrashHandler.install()
        GoldenSignals.coldStart(SystemClock.uptimeMillis() - processStartMs)
    }
}
