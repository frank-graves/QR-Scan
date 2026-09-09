// app/src/main/java/org/foss/lens/infrastructure/CameraLens.kt
package org.foss.lens.infrastructure

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import org.foss.lens.BuildConfig
import org.foss.lens.domain.ScanState
import org.foss.lens.observability.AppLogger
import org.foss.lens.observability.GoldenSignals

// Abierta a propósito: los tests subclasean CameraLens para inyectar un
// ProcessCameraProvider que falla (CameraLensBindFailureTest).
open class CameraLens(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewProvider: Preview.SurfaceProvider,
    private val decoder: CodexDecoder
) : Lens {
    private var cameraProvider: ProcessCameraProvider? = null
    private val analyzerExecutor = Executors.newSingleThreadExecutor()
    private val chaosPrefs: SharedPreferences = context.getSharedPreferences("lens_flags", Context.MODE_PRIVATE)

    // Fault injection to exercise the analyzer error path without broken hardware.
    // The first operand (BuildConfig.DEBUG) disables this in release: the compiler
    // folds it to false and the analyzer block stays dead in production.
    private val chaosEnabled: Boolean = BuildConfig.DEBUG && chaosPrefs.getBoolean("chaos-camera-fail", false)

    private val frameCounter = AtomicInteger(0)

    override suspend fun requestPermissions(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    internal open fun cameraProviderFuture(): ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)

    override fun start(): Flow<ScanState> = callbackFlow {
        val provider = try {
            cameraProviderFuture().await()
        } catch (e: Exception) {
            close(e)
            return@callbackFlow
        }
        cameraProvider = provider

        val preview = Preview.Builder().build()
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val analyzer = ImageAnalysis.Analyzer { imageProxy ->
            try {
                // Every 10 frames in DEBUG (and only when the SharedPreferences flag is set)
                // we throw on purpose to exercise ScanState.Error. It never runs in release.
                if (chaosEnabled && frameCounter.incrementAndGet() % 10 == 0) {
                    throw IllegalStateException("chaos-camera-fail")
                }
                val result = decoder.decode(imageProxy, rotate = true)
                GoldenSignals.analyzerOk()
                if (result != null) trySend(ScanState.Success(result))
            } catch (e: Exception) {
                GoldenSignals.analyzerError()
                trySend(ScanState.Error(e, "Analysis error"))
            } finally {
                imageProxy.close()
            }
        }
        imageAnalysis.setAnalyzer(analyzerExecutor, analyzer)

        // bindToLifecycle (y el surface del preview) deben correr en el hilo
        // principal. El callbackFlow vive en Dispatchers.IO (flowOn) y el
        // resume tras await() puede caer en cualquier hilo, así que forzamos
        // Main aquí. Sin SurfaceProvider el Preview no tiene salida (pantalla
        // negra) y en algunos dispositivos el bind puede fallar.
        var bound = false
        withContext(Dispatchers.Main) {
            try {
                preview.setSurfaceProvider(previewProvider)
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
                bound = true
            } catch (e: Exception) {
                close(e)
            }
        }
        if (!bound) return@callbackFlow

        trySend(ScanState.Idle)
        awaitClose {
            provider.unbindAll()
            cameraProvider = null
            analyzerExecutor.shutdown()
        }
    }.catch { e ->
        // Si esta excepción muere dentro del canal, la causa real se pierde y
        // la UI solo muestra "camera flow failed". Deja el stack en el registro.
        AppLogger.error("CameraLens", "Camera flow failed", e)
        emit(ScanState.Error(e, e.message ?: "Camera flow failed"))
    }.flowOn(Dispatchers.IO)

    override fun stop() {
        cameraProvider?.unbindAll()
    }

    private suspend fun <T> ListenableFuture<T>.await(): T = suspendCancellableCoroutine { cont ->
        // Reanudamos en el hilo que completa el futuro (sin postear al main
        // executor: en Robolectric ese looper no avanza mientras el test hace
        // runBlocking y el test se colgaría). El bind ya salta a Main después.
        addListener({
            if (cont.isActive) {
                try {
                    cont.resume(get())
                } catch (e: Exception) {
                    cont.resumeWith(Result.failure(e))
                }
            }
        }, Executor { it.run() })
    }

    companion object {
        fun isWithinCooldown(now: Long, last: Long, cooldown: Long): Boolean = (now - last) <= cooldown
    }
}
