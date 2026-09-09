package org.foss.lens.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JsonAssetClassifierTest {

    private val classifier = JsonAssetClassifier()

    private val validQr = """
        {
          "schema": "lens.asset.v1",
          "serial": "LNV-TC-M75Q-0001",
          "marca": "Lenovo",
          "modelo": "ThinkCentre M75q",
          "tipo": "desktop",
          "estado": "operativo",
          "componentes": { "cpu": "Ryzen 5 PRO", "ramGb": 16, "discoGb": 512, "so": "Windows 11" },
          "notas": "aula 3"
        }
    """.trimIndent()

    @Test
    fun parse_activovalido_devuelveAsset() {
        val asset = classifier.parse(validQr)
        assertNotNull(asset)
        assertEquals("LNV-TC-M75Q-0001", asset?.serial)
        assertEquals("Lenovo", asset?.marca)
        assertEquals("desktop", asset?.tipo)
        assertEquals(16, asset?.componentes?.ramGb)
        assertEquals("Ryzen 5 PRO", asset?.componentes?.cpu)
        assertEquals(Origen.QR, asset?.origen)
        assertEquals(AssetContract.SCHEMA, asset?.schema)
    }

    @Test
    fun parse_conCamposDesconocidos_noRompe() {
        val qr = validQr.replace("notas\": \"aula 3\"", "campoFuturo\": 123, \"otro\": true")
        val asset = classifier.parse(qr)
        assertNotNull(asset)
        assertEquals("LNV-TC-M75Q-0001", asset?.serial)
    }

    @Test
    fun parse_minimoConSoloSerial_acepta() {
        val qr = """{"schema":"lens.asset.v1","serial":"SERIE-1"}"""
        val asset = classifier.parse(qr)
        assertNotNull(asset)
        assertEquals("SERIE-1", asset?.serial)
        assertNull(asset?.marca)
        assertTrue(asset?.componentes?.ramGb == null)
    }

    @Test
    fun parse_textoPlano_devuelveNull() {
        assertNull(classifier.parse("https://example.com/123"))
        assertNull(classifier.parse("hola mundo"))
        assertNull(classifier.parse(""))
    }

    @Test
    fun parse_jsonSinSchema_devuelveNull() {
        assertNull(classifier.parse("""{"serial":"SERIE-1","marca":"X"}"""))
    }

    @Test
    fun parse_schemaOtraVersion_devuelveNull() {
        assertNull(
            classifier.parse("""{"schema":"lens.asset.v2","serial":"SERIE-1"}""")
        )
    }

    @Test
    fun parse_schemaV1SinSerial_devuelveNull() {
        assertNull(classifier.parse("""{"schema":"lens.asset.v1","marca":"X"}"""))
    }

    @Test
    fun parse_jsonInvalido_devuelveNull() {
        assertNull(classifier.parse("""{"schema": "lens.asset.v1" """.trimIndent()))
    }
}
