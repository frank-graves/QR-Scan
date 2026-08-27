// org/foss/lens/infrastructure/PermissionGate.kt (opcional)
package org.foss.lens.infrastructure

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionGate(private val context: Context) {
    fun hasCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}