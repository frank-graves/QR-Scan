// org/foss/lens/remote/FirestoreVehicleGateway.kt
package org.foss.lens.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.snapshots
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import org.foss.lens.domain.vehicle.PlateContract
import org.foss.lens.domain.vehicle.Vehicle

/**
 * Gateway real del taller sobre Cloud Firestore.
 *
 * La placa es el id del documento (mayúsculas y sin guion): "ABC-123",
 * "abc123" y "ABC123" apuntan al mismo doc y el typo no fabrica fichas
 * duplicadas. No se guarda como campo: el id ya ES la placa.
 *
 * El contenido del documento sigue el contrato de la tarea en español
 * (cliente, marca, modelo, año, color, ultimaFecha, proximaFecha); las
 * fechas viajan como texto ISO-8601 a propósito — Firestore `Timestamp`
 * mete zona horaria donde no la queremos (fecha de calendario del taller,
 * no un instante).
 */
class FirestoreVehicleGateway(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : VehicleGateway {

    override suspend fun findVehicleByPlate(plate: String): Vehicle? {
        val key = PlateContract.normalize(plate) ?: return null
        val snapshot = firestore.collection(COLLECTION).document(key).get().suspendAwait()
        return snapshot.toVehicle()
    }

    override suspend fun upsert(vehicle: Vehicle) {
        val key = PlateContract.normalize(vehicle.plate)
            ?: error("Placa inválida al guardar: ${vehicle.plate}")
        firestore.collection(COLLECTION)
            .document(key)
            .set(vehicle.toDocument(), SetOptions.merge())
            .suspendAwait()
    }

    /**
     * Escucha viva: cada cambio en la colección reemite la lista completa.
     *
     * Los errores (reglas de seguridad, red) NO se tragan aquí: fluyen al
     * ViewModel, que los muestra. Una lista vacía parecería "todo al día"
     * cuando en realidad Firestore está negando la lectura.
     */
    override fun observeAll(): Flow<List<Vehicle>> =
        firestore.collection(COLLECTION)
            .snapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { doc -> doc.toVehicle() } }

    /** Mapper privado: el dominio nunca ve tipos de Firestore. */
    private fun Vehicle.toDocument(): Map<String, Any?> = mapOf(
        FIELD_CLIENT to client,
        FIELD_BRAND to brand,
        FIELD_MODEL to model,
        FIELD_YEAR to year,
        FIELD_COLOR to color,
        FIELD_LAST_SERVICE to lastServiceDate,
        FIELD_NEXT_SERVICE to nextServiceDate
    )

    /** Mapper privado: la placa vive en el id del documento, no en un campo. */
    private fun DocumentSnapshot.toVehicle(): Vehicle? {
        if (!exists()) return null
        val plate = PlateContract.normalize(id) ?: return null
        return Vehicle(
            plate = plate,
            client = getString(FIELD_CLIENT),
            brand = getString(FIELD_BRAND),
            model = getString(FIELD_MODEL),
            year = getLong(FIELD_YEAR)?.toInt(),
            color = getString(FIELD_COLOR),
            lastServiceDate = getString(FIELD_LAST_SERVICE),
            nextServiceDate = getString(FIELD_NEXT_SERVICE)
        )
    }

    private companion object {
        const val TAG = "VehicleGateway"
        const val COLLECTION = "vehicles"

        // Claves del documento según la plantilla de la tarea (es-ES):
        // la placa es el id y no aparece como campo.
        const val FIELD_CLIENT = "cliente"
        const val FIELD_BRAND = "marca"
        const val FIELD_MODEL = "modelo"
        const val FIELD_YEAR = "año"
        const val FIELD_COLOR = "color"
        const val FIELD_LAST_SERVICE = "ultimaFecha"
        const val FIELD_NEXT_SERVICE = "proximaFecha"
    }
}

/**
 * firebase-firestore-ktx (BOM 32) ya no trae la extensión `await()`; en vez
 * de sumar kotlinx-coroutines-play-services por tres llamadas, este puente
 * sobre la Task basta y no añade dependencias al árbol.
 *
 * Se resuelve con `resumeWith` (miembro de Continuation) a propósito: las
 * extensiones top-level `kotlinx.coroutines.resume`/`resumeWithException` no
 * son API pública en coroutines 1.7 y rompen el compilador.
 */
private suspend fun <T> Task<T>.suspendAwait(): T =
    suspendCancellableCoroutine { continuation ->
        // Un solo listener: la Task ya distingue éxito, fallo y cancelación.
        addOnCompleteListener { task ->
            if (!continuation.isActive) return@addOnCompleteListener
            if (task.isSuccessful) {
                continuation.resumeWith(Result.success(task.result))
            } else {
                continuation.resumeWith(
                    Result.failure(task.exception ?: RuntimeException("Tarea de Firestore cancelada"))
                )
            }
        }
    }
