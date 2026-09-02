// app/src/test/java/org/foss/lens/presentation/ScanVMTest.kt
package org.foss.lens.presentation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.foss.lens.domain.Archive
import org.foss.lens.domain.Codex
import org.foss.lens.domain.ScanState
import org.foss.lens.infrastructure.Lens
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanVMTest {

    private val dispatcher = StandardTestDispatcher()

    private class FakeLens : Lens {
        var startCallCount = 0
            private set
        private val stateFlow = MutableStateFlow<ScanState>(ScanState.Idle)

        override fun start(): Flow<ScanState> {
            startCallCount++
            return stateFlow
        }

        override fun stop() {}

        override suspend fun requestPermissions(): Boolean = true
    }

    private class FakeArchive : Archive {
        override suspend fun save(entry: Codex): Long = 0L
        override suspend fun all(): List<Codex> = emptyList()
        override suspend fun delete(id: Long) {}
        override suspend fun clear() {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startScanningCalledTwiceInvokesLensStartOnce() = runTest(dispatcher) {
        val fakeLens = FakeLens()
        val fakeArchive = FakeArchive()
        val vm = ScanVM(fakeLens, fakeArchive)

        vm.startScanning()
        vm.startScanning()
        advanceUntilIdle()

        assertEquals(1, fakeLens.startCallCount)
    }
}
