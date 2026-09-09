package org.foss.lens.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.readText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.foss.lens.domain.Asset
import org.foss.lens.domain.Components
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MockApiGatewayTest {

    private fun engineClient(assertBody: (String) -> Unit): HttpClient {
        val engine = MockEngine { request ->
            assertEquals("POST", request.method.value)
            assertEquals("/api/v1/assets", request.url.encodedPath)
            assertBody(bodyText(request.body))
            respond(
                content = """{"id":"server-id-42","createdAt":"2026-09-06T00:00:00Z"}""",
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        return HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }

    @Test
    fun push_enviaElActivoYDevuelveElId() = runBlocking {
        var captured = ""
        val gateway = MockApiGateway(
            "https://demo.mockapi.io/api/v1",
            engineClient { captured = it }
        )
        val id = gateway.push(
            Asset(
                serial = "LNV-1",
                marca = "Lenovo",
                componentes = Components(ramGb = 16)
            )
        )
        assertEquals("server-id-42", id)
        assertTrue(captured.contains("\"serial\":\"LNV-1\""))
        assertTrue(captured.contains("\"schema\":\"lens.asset.v1\""))
        assertTrue(captured.contains("\"origen\":\"qr\""))
        assertTrue(captured.contains("\"ramGb\":16"))
        assertFalse(captured.contains("localId"))
        assertFalse(captured.contains("syncState"))
    }

    @Test
    fun sinUrlConfigurada_noEstaConfigurado() {
        val gateway = MockApiGateway("", engineClient {})
        assertFalse(gateway.isConfigured)
    }

    @Test
    fun conUrlConfigurada_estaConfigurado() {
        val gateway = MockApiGateway("https://demo.mockapi.io", engineClient {})
        assertTrue(gateway.isConfigured)
    }

    private suspend fun bodyText(body: OutgoingContent): String = when (body) {
        is OutgoingContent.ByteArrayContent -> body.bytes().decodeToString()
        is OutgoingContent.ReadChannelContent ->
            body.readFrom().readRemaining().readText()
        else -> ""
    }
}
