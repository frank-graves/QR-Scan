// app/src/main/java/org/foss/lens/infrastructure/CodexDecoder.kt
package org.foss.lens.infrastructure

import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.foss.lens.domain.Codex
import java.nio.ByteBuffer

class CodexDecoder {
    private val reader = QRCodeReader()

    fun decode(image: ImageProxy): Codex? {
        val planes = image.planes
        if (planes.isEmpty()) return null

        val yPlane = planes[0]
        val buffer = yPlane.buffer
        val rowStride = yPlane.rowStride
        val pixelStride = yPlane.pixelStride
        val width = image.width
        val height = image.height

        // Crear array contiguo de tamaño width * height
        val yData = ByteArray(width * height)

        // Optimización: si el stride es exacto, copiar todo de una vez
        if (rowStride == width && pixelStride == 1) {
            // El buffer puede tener más datos (padding), pero solo tomamos width*height
            val pos = buffer.position()
            buffer.get(yData, 0, width * height)
            // Restaurar posición (opcional, pero no necesario)
        } else {
            // Copia línea a línea respetando stride y pixelStride
            val bufferPos = buffer.position()
            var rowOffset = 0
            for (row in 0 until height) {
                val rowStart = bufferPos + row * rowStride
                var col = 0
                while (col < width) {
                    val srcPos = rowStart + col * pixelStride
                    yData[rowOffset + col] = buffer.get(srcPos)
                    col++
                }
                rowOffset += width
            }
        }

        val source = PlanarYUVLuminanceSource(yData, width, height, 0, 0, width, height, false)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        return try {
            val result = reader.decode(bitmap)
            Codex(
                payload = result.text,
                format = result.barcodeFormat.name,
                timestamp = java.time.Instant.now()
            )
        } catch (e: Exception) {
            null
        }
    }
}