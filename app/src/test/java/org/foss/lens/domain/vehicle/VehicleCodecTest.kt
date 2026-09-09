// app/src/test/java/org/foss/lens/domain/vehicle/VehicleCodecTest.kt
package org.foss.lens.domain.vehicle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleCodecTest {

    private val codec = JsonVehicleCodec()

    @Test
    fun qrCompleto_autocompletaTodaLaFicha() {
        val payload = """
            {"schema":"talara.vehicle.v1","plate":"ABC-123","client":"Juan Pérez",
             "brand":"Toyota","model":"Corolla","year":2018,"color":"Blanco",
             "lastServiceDate":"2026-01-15","nextServiceDate":"2026-04-15"}
        """.trimIndent()
        val vehicle = codec.decode(payload)

        assertEquals("ABC123", vehicle?.plate)
        assertEquals("Juan Pérez", vehicle?.client)
        assertEquals("Toyota", vehicle?.brand)
        assertEquals("Corolla", vehicle?.model)
        assertEquals(2018, vehicle?.year)
        assertEquals("Blanco", vehicle?.color)
        assertEquals("2026-01-15", vehicle?.lastServiceDate)
        assertEquals("2026-04-15", vehicle?.nextServiceDate)
        assertFalse(vehicle!!.isBarePlateScan())
    }

    @Test
    fun qrConAnioEnTexto_seAceptaIgual() {
        val payload = """{"schema":"talara.vehicle.v1","plate":"ABC123","year":"2018"}"""
        val vehicle = codec.decode(payload)
        assertEquals(2018, vehicle?.year)
        assertFalse(vehicle!!.isBarePlateScan())
    }

    @Test
    fun qrJsonSoloPlaca_esBarePlate() {
        val payload = """{"schema":"talara.vehicle.v1","plate":"abc-123"}"""
        val vehicle = codec.decode(payload)
        assertEquals("ABC123", vehicle?.plate)
        assertTrue(vehicle!!.isBarePlateScan())
    }

    @Test
    fun placaPeladaSinJson_rellenaSoloLaPlaca() {
        val vehicle = codec.decode("  abc-123  ")
        assertEquals("ABC123", vehicle?.plate)
        assertTrue(vehicle!!.isBarePlateScan())
    }

    @Test
    fun schemaDeInventario_noSeLoRobaElTaller() {
        val payload = """{"schema":"lens.asset.v1","serial":"PC-01","marca":"Dell"}"""
        assertNull(codec.decode(payload))
    }

    @Test
    fun jsonSinSchemaDelTaller_noCuenta() {
        // Aunque traiga placa, sin el schema versionado no es un QR del taller.
        assertNull(codec.decode("""{"plate":"ABC123"}"""))
    }

    @Test
    fun basuraNoEsVehiculo() {
        assertNull(codec.decode(""))
        assertNull(codec.decode("https://ejemplo.com/qr"))
        assertNull(codec.decode("12345"))
        assertNull(codec.decode("ABC"))
    }

    @Test
    fun qrConPlacaInvalida_noCreaVehiculo() {
        val payload = """{"schema":"talara.vehicle.v1","plate":"no-es-placa"}"""
        assertNull(codec.decode(payload))
    }

    @Test
    fun normalizacion_uneGuiónMayusculasYEspacios() {
        assertNull(PlateContract.normalize(null))
        assertNull(PlateContract.normalize("  "))
        // El guion y los espacios son cosméticos: todas resuelven al mismo id.
        assertEquals("ABC123", PlateContract.normalize("abc-123"))
        assertEquals("ABC123", PlateContract.normalize(" abc 123 "))
        assertEquals("XYZ1234", PlateContract.normalize("xyz1234"))
        // Formato nuevo (A1B-234) de los autos modernos: la P1A-458 del reporte.
        assertEquals("P1A458", PlateContract.normalize("p1a-458"))
        assertEquals("M1A234", PlateContract.normalize("m1a-234"))
        // Lo que no es placa bajo ningún formato se rechaza.
        assertNull(PlateContract.normalize("xyz12")) // 2 dígitos no alcanzan
        assertNull(PlateContract.normalize("1AB234")) // arranca con dígito
        assertNull(PlateContract.normalize("A1B23")) // faltan dígitos al cierre
    }

    @Test
    fun jsonRealDeTalara_conPlacaFormatoNuevo_abreTaller() {
        // Payload literal del reporte: JSON impecable que la app mandaba al
        // historial. La placa P1A-458 (formato nuevo A1B-234) no cumplía el
        // patrón clásico ABC-123 y el taller lo rechazaba con un null mudo.
        val payload = """
            {"schema":"talara.vehicle.v1","plate":"P1A-458","client":"Carlos Mendoza",
             "brand":"Nissan","model":"Versa","year":2021,"color":"Plata",
             "lastServiceDate":"2026-01-15","nextServiceDate":"2026-07-15"}
        """.trimIndent()
        val vehicle = codec.decode(payload)

        assertEquals("P1A458", vehicle?.plate)
        assertEquals("Carlos Mendoza", vehicle?.client)
        assertEquals("Nissan", vehicle?.brand)
        assertEquals("Versa", vehicle?.model)
        assertEquals(2021, vehicle?.year)
        assertEquals("Plata", vehicle?.color)
        assertEquals("2026-01-15", vehicle?.lastServiceDate)
        assertEquals("2026-07-15", vehicle?.nextServiceDate)
        assertFalse(vehicle!!.isBarePlateScan())
    }

    @Test
    fun vencimientoIso_seComparaLexicograficamente() {
        val vehicle = Vehicle(plate = "ABC123", nextServiceDate = "2025-01-10")
        assertTrue(vehicle.isServiceDueOnOrBefore("2025-01-10"))
        assertTrue(vehicle.isServiceDueOnOrBefore("2026-05-01"))
        assertFalse(vehicle.isServiceDueOnOrBefore("2024-12-31"))
    }

    @Test
    fun sinProximaFecha_nuncaVence() {
        val fresh = Vehicle(plate = "ABC123")
        assertFalse(fresh.isServiceDueOnOrBefore("2099-01-01"))
        assertTrue(fresh.isBarePlateScan())
    }

    @Test
    fun jsonConComillasTipograficas_noSeDesviaAlHistorial() {
        // Algunos generadores de QR embellecen el JSON con comillas curvas;
        // un solo carácter así tumbaba `parseToJsonElement` y el QR moría en
        // el historial como "contenido genérico" en vez de abrir el taller.
        val payload = "{\u201Cschema\u201D:\u201Ctalara.vehicle.v1\u201D,\u201Cplate\u201D:\u201CABC-123\u201D}"
        val vehicle = codec.decode(payload)

        assertEquals("ABC123", vehicle?.plate)
        assertTrue(vehicle!!.isBarePlateScan())
    }

    @Test
    fun jsonConEspaciosInvisibles_seDecodificaIgual() {
        // BOM al inicio y zero-width al final: basura invisible de copiar/pegar
        // que también hacía que el payload ni siquiera intentara el JSON.
        val payload = "\uFEFF{\"schema\":\"talara.vehicle.v1\",\"plate\":\"ABC123\",\"client\":\"Juan\u00A0Pérez\"}\u200B"
        val vehicle = codec.decode(payload)

        assertEquals("ABC123", vehicle?.plate)
        // El NBSP dentro del valor se normaliza a espacio común.
        assertEquals("Juan Pérez", vehicle?.client)
        assertFalse(vehicle!!.isBarePlateScan())
    }

    @Test
    fun placaPeladaConCaracteresInvisibles_seNormalizaIgual() {
        assertEquals("ABC123", codec.decode("ABC\u200B-123\u00A0")?.plate)
        assertEquals("ABC123", codec.decode("\uFEFFabc-123")?.plate)
    }

    @Test
    fun cleanQrPayload_normalizaSinTocarContenido() {
        assertEquals("\"ABC-123\"", "\u201CABC-123\u201D".cleanQrPayload())
        assertEquals("a  b", "a\u00A0 b".cleanQrPayload())
        assertEquals("ABC123", " \u200B\uFEFFABC123\u00A0".cleanQrPayload())
    }
}
