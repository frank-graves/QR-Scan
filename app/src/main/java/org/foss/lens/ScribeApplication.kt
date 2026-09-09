package org.foss.lens

import android.app.Application
import android.os.SystemClock
import androidx.camera.core.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.foss.lens.data.sync.AssetSyncer
import org.foss.lens.di.appModule
import org.foss.lens.infrastructure.CameraLens
import org.foss.lens.infrastructure.CodexDecoder
import org.foss.lens.infrastructure.Lens
import org.foss.lens.infrastructure.NetworkMonitor
import org.foss.lens.observability.AppLogger
import org.foss.lens.observability.CrashHandler
import org.foss.lens.observability.GoldenSignals
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScribeApplication : Application(), KoinComponent {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val syncer: AssetSyncer by inject()
    private val networkMonitor: NetworkMonitor by inject()
    private val decoder: CodexDecoder by inject()

    override fun onCreate() {
        super.onCreate()
        AppLogger.init(this)
        CrashHandler.install()

        startKoin {
            androidContext(this@ScribeApplication)
            modules(appModule)
        }

        GoldenSignals.coldStart(SystemClock.uptimeMillis())
        startOfflineSyncLoop()
    }

    /**
     * La cámara se construye por pantalla: la Activity le pasa su LifecycleOwner
     * y el SurfaceProvider del PreviewView que Compose monta en el escáner.
     */
    fun createLens(
        lifecycleOwner: androidx.lifecycle.LifecycleOwner,
        previewProvider: Preview.SurfaceProvider
    ): Lens = CameraLens(this, lifecycleOwner, previewProvider, decoder)

    private fun startOfflineSyncLoop() {
        appScope.launch {
            networkMonitor.online().collect { isOnline ->
                if (isOnline) {
                    AppLogger.debug("LensApp", "Red disponible; drenando cola de sync")
                    syncer.flushPending()
                }
            }
        }
    }
}
