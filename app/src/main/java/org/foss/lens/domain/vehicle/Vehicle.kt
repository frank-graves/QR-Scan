// org/foss/lens/domain/vehicle/Vehicle.kt
package org.foss.lens.domain.vehicle

import kotlinx.serialization.Serializable
import java.util.Locale

/**
 * Un vehículo del taller. `plate` es la clave natural: también es el id del
 * documento en Firestore, así que dos QR con la misma placa jamás duplican la
 * ficha. El resto son datos de mostrador que el mecánico completa la primera
 * vez que atiende el vehículo.
 */
@Serializable
data class Vehicle(
    val plate: String,
    val client: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val year: Int? = null,
    val color: String? = null,
    /** Fecha del último cambio de aceite, ISO-8601 (yyyy-MM-dd). */
    val lastServiceDate: String? = null,
    /**
     * Fecha recomendada del próximo cambio, ISO-8601.
     *
     * Fechas de calendario como texto y no como `Timestamp`: el taller vive en
     * una sola zona horaria y comparar strings ISO con ceros a la izquierda es
     * ordenar fechas sin reloj ni conversión. Firestore solo las guarda.
     */
    val nextServiceDate: String? = null
)

/** Reglas de la placa del taller. Compartidas por codec, gateway y UI. */
object PlateContract {

    // Dos formatos conviven en el parque automotor peruano: el clásico ABC-123
    // (2-3 letras + 3-4 dígitos) y el nuevo A1B-234 (letra-dígito-letra +
    // 3 dígitos) que ya llevan los autos modernos — la P1A-458 que destapó el
    // bug es de este segundo. El guion y los espacios son cosméticos: ABC-123,
    // ABC123 y ABC 123 describen la misma placa y resuelven al mismo documento.
    private val VALID_PLATE = Regex("^([A-Z]{2,3}[0-9]{3,4}|[A-Z][0-9][A-Z][0-9]{3})$")

    /** Placa canónica (mayúsculas, sin guion ni espacios) o null si no es placa. */
    fun normalize(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val candidate = raw.trim().uppercase(Locale.ROOT).replace(Regex("[\\s-]"), "")
        return candidate.takeIf { VALID_PLATE.matches(it) }
    }
}

private const val ISO_DATE_LENGTH = 10

/**
 * ¿Ya toca pasar por el foso? Cierto cuando `nextServiceDate` existe y es
 * anterior o igual a la fecha de referencia. Como ambas son ISO-8601 fijas
 * (año-mes-día con ceros), la comparación de strings ES comparación de fechas:
 * el dominio no necesita un reloj inyectado para decidir.
 */
fun Vehicle.isServiceDueOnOrBefore(referenceDateIso: String): Boolean {
    val next = nextServiceDate ?: return false
    return next.length == ISO_DATE_LENGTH && next <= referenceDateIso
}

/**
 * ¿El QR trajo solo la placa (texto pelado o JSON mínimo)? Si es así, mejor
 * consultar la ficha en Firestore: una placa desnuda no debe pisar datos que
 * el servidor ya conoce con más detalle.
 */
fun Vehicle.isBarePlateScan(): Boolean =
    client == null && brand == null && model == null && year == null && color == null &&
        lastServiceDate == null && nextServiceDate == null
