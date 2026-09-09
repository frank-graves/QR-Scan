// org/foss/lens/domain/vehicle/VehicleRepository.kt
package org.foss.lens.domain.vehicle

import kotlinx.coroutines.flow.Flow

/**
 * Puerta de entrada al taller desde la UI, espejo de `AssetRepository`.
 *
 * La implementación vive en `data/vehicle/` y, a diferencia del inventario,
 * no hay cola offline-first detrás: Firestore persiste en disco y reenvía solo,
 * así que el contrato es un pasamanos directo a la nube.
 */
interface VehicleRepository {

    /** Ficha del vehículo, o null si la placa aún no tiene ficha en el taller. */
    suspend fun findVehicleByPlate(plate: String): Vehicle?

    /**
     * Registra el servicio: actualiza fechas y datos del vehículo, creando la
     * ficha si es la primera visita. Devuelve el resultado para que la UI
     * distinga "guardado" de "sin conexión".
     */
    suspend fun saveService(vehicle: Vehicle): Result<Unit>

    /** Vehículos cuyo próximo servicio ya venció o toca hoy, en vivo. */
    fun getPendingVehicles(): Flow<List<Vehicle>>
}
