// org/foss/lens/domain/AssetClassifier.kt
package org.foss.lens.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Decide si un payload escaneado es un activo de inventario o "otra cosa".
 *
 * El escáner jamás debe saber qué es una PC: solo entrega el texto y este
 * clasificador interpreta. Si mañana nace `lens.asset.v2`, se añade un
 * clasificador; el resto de la app no cambia.
 */
interface AssetClassifier {
    /** Devuelve el activo si el payload es JSON `lens.asset.v1`; si no, null. */
    fun parse(raw: String): Asset?
}

/**
 * Parser tolerante: ignora campos desconocidos, acepta valores ausentes y
 * nunca lanza. Un QR que no cumpla el contrato simplemente se trata como
 * contenido genérico.
 */
class JsonAssetClassifier(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
) : AssetClassifier {

    /** Espejo decodificable del JSON del QR (solo la parte de negocio). */
    @Serializable
    private data class Wire(
        val marca: String? = null,
        val modelo: String? = null,
        val tipo: String? = null,
        val estado: String? = null,
        val ubicacion: String? = null,
        val componentes: Components = Components(),
        val notas: String? = null
    )

    override fun parse(raw: String): Asset? {
        if (raw.isBlank()) return null
        val root = runCatching { json.parseToJsonElement(raw).jsonObject }.getOrNull() ?: return null

        val schema = root[AssetContract.SCHEMA_KEY]?.jsonPrimitive?.contentOrNull
        if (schema != AssetContract.SCHEMA) return null
        val serial = root[AssetContract.SERIAL_KEY]?.jsonPrimitive?.contentOrNull
        if (serial.isNullOrBlank()) return null

        val wire = runCatching { json.decodeFromJsonElement<Wire>(root) }.getOrNull() ?: return null
        return Asset(
            serial = serial,
            marca = wire.marca?.ifBlank { null },
            modelo = wire.modelo?.ifBlank { null },
            tipo = wire.tipo?.ifBlank { null },
            estado = wire.estado?.ifBlank { null },
            ubicacion = wire.ubicacion?.ifBlank { null },
            componentes = wire.componentes,
            notas = wire.notas?.ifBlank { null }
        )
    }
}
