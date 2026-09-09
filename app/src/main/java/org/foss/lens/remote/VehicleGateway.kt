// org/foss/lens/remote/VehicleGateway.kt
package org.foss.lens.remote

import kotlinx.coroutines.flow.Flow
import org.foss.lens.domain.vehicle.Vehicle

/**
 * Contrato del backend del taller. Copia la forma de `AssetSyncGateway` pero
 * con lectura: aquí primero se busca la ficha por placa y después se escribe
 * el servicio. Si mañana el taller migra de Firestore a otra nube, solo
 * cambia la implementación.
 *
 * Sin bandera `isConfigured` a propósito: mockAPI la necesitaba por su "modo
 * demo" con URL vacía; Firestore o está en el build (google-services.json) o
 * el plugin de Google no deja compilar — la bandera sería humo.
 */
interface VehicleGateway {

    /** Ficha completa del vehículo, o null si la placa no está registrada. */
    suspend fun findVehicleByPlate(plate: String): Vehicle?

    /** Crea o actualiza la ficha completa (merge: una sola escritura para ambos casos). */
    suspend fun upsert(vehicle: Vehicle)

    /** Flujo en vivo de toda la colección de vehículos. */
    fun observeAll(): Flow<List<Vehicle>>
}
