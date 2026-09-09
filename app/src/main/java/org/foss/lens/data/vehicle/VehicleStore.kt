// org/foss/lens/data/vehicle/VehicleStore.kt
package org.foss.lens.data.vehicle

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.foss.lens.domain.vehicle.Vehicle
import org.foss.lens.domain.vehicle.VehicleRepository
import org.foss.lens.domain.vehicle.isServiceDueOnOrBefore
import org.foss.lens.remote.VehicleGateway
import java.time.LocalDate

/**
 * Implementación del repositorio del taller.
 *
 * A diferencia del inventario no hay Room ni cola PENDING/SYNCED/FAILED:
 * Firestore ya guarda en disco y reenvía al recuperar la red, así que esto es
 * un pasamanos con un filtro. Sigue la convención del proyecto (data → remote,
 * domain jamás importa esta capa).
 */
class VehicleStore(private val gateway: VehicleGateway) : VehicleRepository {

    override suspend fun findVehicleByPlate(plate: String): Vehicle? =
        gateway.findVehicleByPlate(plate)

    override suspend fun saveService(vehicle: Vehicle): Result<Unit> = try {
        gateway.upsert(vehicle)
        Result.success(Unit)
    } catch (boom: CancellationException) {
        // El ViewModel murió a mitad del guardado (se salió de la pantalla):
        // `runCatching` la habría convertido en un "No se guardó" fantasma.
        throw boom
    } catch (boom: Exception) {
        Result.failure(boom)
    }

    override fun getPendingVehicles(): Flow<List<Vehicle>> =
        gateway.observeAll().map { fleet ->
            // "Hoy" se fija al suscribirse; el filtro usa el calendario del
            // taller, no un instante UTC: quien vence, vence en el taller.
            val today = LocalDate.now().toString()
            fleet.filter { vehicle -> vehicle.isServiceDueOnOrBefore(today) }
        }
}
