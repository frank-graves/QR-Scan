// org/foss/lens/remote/MockApiGateway.kt
package org.foss.lens.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.foss.lens.domain.Asset
import org.foss.lens.domain.Origen

/**
 * Gateway contra mockAPI (colección `/assets`). Sin URL configurada se declara
 * no configurado y el sync no intenta nada: útil para demos sin backend.
 */
class MockApiGateway(
    private val baseUrl: String,
    private val client: HttpClient
) : AssetSyncGateway {

    override val isConfigured: Boolean = baseUrl.isNotBlank()

    override suspend fun push(asset: Asset): String {
        require(isConfigured) { "mockAPI sin configurar: define lens.mockapiUrl en gradle.properties" }
        val response = client.post("${baseUrl.trimEnd('/')}/assets") {
            contentType(ContentType.Application.Json)
            setBody(asset.toWire())
        }
        if (!response.status.isSuccess()) {
            error("HTTP ${response.status.value}: ${response.bodyAsText().take(200)}")
        }
        // mockAPI devuelve el recurso creado con su "id".
        val raw = response.bodyAsText()
        val root = runCatching { Json.parseToJsonElement(raw).jsonObject }.getOrNull()
        return root?.get("id")?.jsonPrimitive?.contentOrNull ?: ""
    }

    @Serializable
    private data class Wire(
        val schema: String,
        val serial: String,
        val marca: String? = null,
        val modelo: String? = null,
        val tipo: String? = null,
        val estado: String? = null,
        val ubicacion: String? = null,
        val cpu: String? = null,
        @SerialName("ramGb") val ramGb: Int? = null,
        @SerialName("discoGb") val discoGb: Int? = null,
        val so: String? = null,
        val notas: String? = null,
        val origen: Origen
    )

    private fun Asset.toWire() = Wire(
        schema = schema,
        serial = serial,
        marca = marca,
        modelo = modelo,
        tipo = tipo,
        estado = estado,
        ubicacion = ubicacion,
        cpu = componentes.cpu,
        ramGb = componentes.ramGb,
        discoGb = componentes.discoGb,
        so = componentes.so,
        notas = notas,
        origen = origen
    )

    companion object {
        fun httpClient(): HttpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 12_000
                connectTimeoutMillis = 8_000
            }
        }
    }
}
