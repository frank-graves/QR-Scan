package org.foss.lens.infrastructure

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.core.app.ApplicationProvider
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.foss.lens.domain.ScanState
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class CameraLensBindFailureTest {

    @Test
    fun bindFailure_emitsScanStateError() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val lifecycleOwner = object : LifecycleOwner {
            private val registry = LifecycleRegistry(this)
            init { registry.currentState = Lifecycle.State.RESUMED }
            override val lifecycle: Lifecycle get() = registry
        }
        val failure = IllegalStateException("camera provider exploded")
        val lens = FailingProviderLens(context, lifecycleOwner, CodexDecoder(), failure)

        val state = lens.start().first()

        assertTrue("Expected ScanState.Error", state is ScanState.Error)
        assertTrue(
            "Cause should be the injected exception",
            (state as ScanState.Error).cause === failure
        )
    }

    private class FailingProviderLens(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        decoder: CodexDecoder,
        private val failure: Throwable
    ) : CameraLens(context, lifecycleOwner, decoder) {
        override fun cameraProviderFuture(): ListenableFuture<ProcessCameraProvider> =
            FailingListenableFuture(failure)
    }

    private class FailingListenableFuture<T>(
        private val failure: Throwable
    ) : ListenableFuture<T> {
        override fun addListener(listener: Runnable, executor: Executor) {
            executor.execute(listener)
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false

        override fun isCancelled(): Boolean = false

        override fun isDone(): Boolean = true

        override fun get(): T {
            throw failure
        }

        override fun get(timeout: Long, unit: TimeUnit): T {
            throw failure
        }
    }
}
