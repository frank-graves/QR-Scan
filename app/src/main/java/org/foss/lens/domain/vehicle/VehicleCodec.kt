// org/foss/lens/domain/vehicle/VehicleCodec.kt
package org.foss.lens.domain.vehicle

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Contrato del QR del taller. Igual que `AssetContract`, versionado: si mañana
 * el QR quiere traer más campos, nace `talara.vehicle.v2` y el código viejo
 * sigue leyendo v1 sin romperse.
 *
 * Las claves del JSON son las del modelo: `plate` (obligatoria) y opcionales
 * `client`, `brand`, `model`, `year`, `color`, `lastServiceDate`,
 * `nextServiceDate`. Las fechas viajan ISO-8601, como en Firestore.
 */
object VehicleQrContract {
    const val SCHEMA = "talara.vehicle.v1"
    const val SCHEMA_KEY = "schema"
    const val PLATE_KEY = "plate"
}

/** Interpreta el payload de un QR y responde con la ficha, si es del taller. */
interface VehicleCodec {

    /**
     * Ficha con los campos que traiga el QR (`plate` siempre presente) o null
     * si el payload no es un QR del taller. Los opcionales llegan rellenos
     * para que el formulario se autocomplete y el mecánico solo revise.
     */
    fun decode(raw: String): Vehicle?
}

/**
 * Parser tolerante, espejo de `JsonAssetClassifier` pero para el taller.
 * Acepta el JSON versionado completo y, de regalo, un QR que codifique la
 * placa pelada: no todos los talleres generan JSON, muchos imprimen el texto
 * plano nomás.
 */
class JsonVehicleCodec(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
) : VehicleCodec {

    /**
     * Espejo decodificable del JSON del QR. `year` se lee aparte: puede llegar
     * como número o como texto y un fallo de tipo no debe tumbar la ficha.
     */
    @Serializable
    private data class Wire(
        val plate: String? = null,
        val client: String? = null,
        val brand: String? = null,
        val model: String? = null,
        val color: String? = null,
        val lastServiceDate: String? = null,
        val nextServiceDate: String? = null
    )

    override fun decode(raw: String): Vehicle? {
        if (raw.isBlank()) return null
        val trimmed = raw.trim()

        val root = runCatching { json.parseToJsonElement(trimmed).jsonObject }.getOrNull()
        if (root != null) {
            val schema = root[VehicleQrContract.SCHEMA_KEY]?.jsonPrimitive?.contentOrNull
            if (schema != VehicleQrContract.SCHEMA) return null
            val wire = runCatching { json.decodeFromJsonElement<Wire>(root) }.getOrNull()
                ?: return null
            val plate = PlateContract.normalize(wire.plate) ?: return null
            return Vehicle(
                plate = plate,
                client = wire.client?.ifBlank { null },
                brand = wire.brand?.ifBlank { null },
                model = wire.model?.ifBlank { null },
                year = readYear(root),
                color = wire.color?.ifBlank { null },
                lastServiceDate = wire.lastServiceDate?.ifBlank { null },
                nextServiceDate = wire.nextServiceDate?.ifBlank { null }
            )
        }

        // Sin JSON: el QR codifica solo la placa pelada.
        val plate = PlateContract.normalize(trimmed) ?: return null
        return Vehicle(plate = plate)
    }

    private fun readYear(root: JsonObject): Int? {
        val element = root["year"] ?: return null
        val primitive = runCatching { element.jsonPrimitive }.getOrNull() ?: return null
        return primitive.intOrNull ?: primitive.contentOrNull?.trim()?.toIntOrNull()
    }
}
