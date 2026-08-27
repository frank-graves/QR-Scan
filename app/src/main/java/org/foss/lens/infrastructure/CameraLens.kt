// org/foss/lens/infrastructure/CameraLens.kt
package org.foss.lens.infrastructure

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import org.foss.lens.domain.ScanState

class CameraLens(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val decoder: CodexDecoder
) : Lens {
    private var cameraProvider: ProcessCameraProvider? = null
    private val analyzerExecutor = Executors.newSingleThreadExecutor()

    override suspend fun requestPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    override fun start(): Flow<ScanState> = callbackFlow {
        // Esperar el provider con una suspensión real usando ListenableFuture
        val provider = try {
            val future = ProcessCameraProvider.getInstance(context)
            future.await() // nuestra extensión abajo
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
                val result = decoder.decode(imageProxy)
                if (result != null) {
                    trySend(ScanState.Success(result))
                }
            } catch (e: Exception) {
                trySend(ScanState.Error(e, "Error en análisis"))
            } finally {
                imageProxy.close()
            }
        }
        imageAnalysis.setAnalyzer(analyzerExecutor, analyzer)

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        } catch (e: Exception) {
            close(e)
            return@callbackFlow
        }

        trySend(ScanState.Idle)

        awaitClose {
            provider.unbindAll()
            cameraProvider = null
            analyzerExecutor.shutdown()
        }
    }.catch { e -> emit(ScanState.Error(e, "Flujo de cámara falló")) }
        .flowOn(Dispatchers.IO)

    override fun stop() {
        cameraProvider?.unbindAll()
    }

    // Extensión para convertir ListenableFuture a suspensión
    private suspend fun <T> ListenableFuture<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addListener({
            if (continuation.isActive) {
                try {
                    continuation.resume(get())
                } catch (e: Exception) {
                    continuation.resumeWith(Result.failure(e))
                }
            }
        }, ContextCompat.getMainExecutor(context))
    }
}