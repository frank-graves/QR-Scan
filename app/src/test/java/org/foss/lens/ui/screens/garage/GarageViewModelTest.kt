// org/foss/lens/ui/screens/garage/GarageViewModelTest.kt
package org.foss.lens.ui.screens.garage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.foss.lens.domain.vehicle.Vehicle
import org.foss.lens.domain.vehicle.VehicleCodec
import org.foss.lens.domain.vehicle.VehicleRepository
import org.foss.lens.ui.PendingScanHolder
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Regresión del error fantasma: una cancelación interna de la escucha de
 * pendientes (cambiar de tab, re-entrar al tab) se colaba en el catch de
 * negocio y quedaba clavada en "Registrar" como
 * "No se pudo leer los pendientes: StandaloneCoroutine was cancelled".
 *
 * Corre en JVM pura: el repositorio es un fake y el viewModelScope cuelga de
 * Dispatchers.Main, que aquí se sustituye por un dispatcher de test.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GarageViewModelTest {

    private val mainDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun cancellationWhileLeavingPendingListIsNotAnError() = runTest(mainDispatcher.scheduler) {
        // Escucha que entrega un snapshot y luego queda viva en silencio,
        // como Firestore sin novedades: la lista ya se pintó una vez.
        val garage = StubGarage(
            pending = flow {
                emit(emptyList())
                awaitCancellation()
            }
        )
        val vm = GarageViewModel(garage, NullCodec(), PendingScanHolder())

        vm.enterPendingList()
        advanceUntilIdle()
        assertTrue(
            "el primer snapshot debe pintar la lista de pendientes",
            vm.state.value is GarageUiState.PendingServicesList
        )

        // El usuario cambia al tab Registrar: la escucha se corta a propósito.
        vm.leavePendingList()
        advanceUntilIdle() // la cancelación se propaga hasta el collector

        assertFalse(
            "cancelar la escucha al salir del tab no es un fallo del taller ni de la red",
            vm.state.value is GarageUiState.Error
        )
    }

    /** Taller falso: solo la escucha importa; el resto del contrato no se usa aquí. */
    private class StubGarage(
        private val pending: Flow<List<Vehicle>> = flow { awaitCancellation() }
    ) : VehicleRepository {
        override suspend fun findVehicleByPlate(plate: String): Vehicle? = null
        override suspend fun saveService(vehicle: Vehicle): Result<Unit> = Result.success(Unit)
        override fun getPendingVehicles(): Flow<List<Vehicle>> = pending
    }

    /** Codec mudo: ninguna entrada de este test pasa por el decodificador. */
    private class NullCodec : VehicleCodec {
        override fun decode(raw: String): Vehicle? = null
    }
}
