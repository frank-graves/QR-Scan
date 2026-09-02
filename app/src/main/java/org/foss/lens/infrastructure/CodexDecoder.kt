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

    fun decode(image: ImageProxy, rotate: Boolean = false): Codex? {
        val planes = image.planes
        if (planes.isEmpty()) return null

        val yPlane = planes[0]
        val buffer: ByteBuffer = yPlane.buffer
        val rowStride = yPlane.rowStride
        val pixelStride = yPlane.pixelStride
        val width = image.width
        val height = image.height
        val rotation = image.imageInfo.rotationDegrees

        val yData = ByteArray(width * height)
        if (rowStride == width && pixelStride == 1) {
            buffer.get(yData, 0, width * height)
        } else {
            val bufferPos = buffer.position()
            var rowOffset = 0
            for (row in 0 until height) {
                val rowStart = bufferPos + row * rowStride
                var col = 0
                while (col < width) {
                    yData[rowOffset + col] = buffer.get(rowStart + col * pixelStride)
                    col++
                }
                rowOffset += width
            }
        }

        var sourceWidth = width
        var sourceHeight = height
        var sourceData = yData
        if (rotate && rotation != 0) {
            sourceData = rotateYPlane(yData, width, height, rotation)
            if (rotation == 90 || rotation == 270) {
                sourceWidth = height
                sourceHeight = width
            }
        }

        val source = PlanarYUVLuminanceSource(sourceData, sourceWidth, sourceHeight, 0, 0, sourceWidth, sourceHeight, false)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        return try {
            val result = reader.decode(bitmap)
            Codex(payload = result.text, format = result.barcodeFormat.name, timestamp = java.time.Instant.now())
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        internal fun rotateYPlane(yData: ByteArray, width: Int, height: Int, rotation: Int): ByteArray {
            return when (rotation) {
                90 -> {
                    val outW = height
                    val outH = width
                    val out = ByteArray(outW * outH)
                    for (y in 0 until outH) {
                        for (x in 0 until outW) {
                            val inX = y
                            val inY = height - 1 - x
                            out[y * outW + x] = yData[inY * width + inX]
                        }
                    }
                    out
                }
                270 -> {
                    val outW = height
                    val outH = width
                    val out = ByteArray(outW * outH)
                    for (y in 0 until outH) {
                        for (x in 0 until outW) {
                            val inX = width - 1 - y
                            val inY = x
                            out[y * outW + x] = yData[inY * width + inX]
                        }
                    }
                    out
                }
                180 -> {
                    val outW = width
                    val outH = height
                    val out = ByteArray(outW * outH)
                    for (y in 0 until outH) {
                        for (x in 0 until outW) {
                            val inX = width - 1 - x
                            val inY = height - 1 - y
                            out[y * outW + x] = yData[inY * width + inX]
                        }
                    }
                    out
                }
                else -> yData
            }
        }
    }
}
