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
        assertEquals("A1B234", PlateContract.normalize("a1b-234"))
        assertEquals("XYZ12", PlateContract.normalize("xyz12"))
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
}
