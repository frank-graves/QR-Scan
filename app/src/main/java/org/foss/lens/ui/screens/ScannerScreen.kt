// org/foss/lens/ui/screens/ScannerScreen.kt
package org.foss.lens.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.foss.lens.ScribeApplication
import org.foss.lens.domain.ScanState
import org.foss.lens.ui.LensRoutes
import org.foss.lens.ui.ScannerEvent
import org.koin.androidx.compose.koinViewModel

private fun hasCameraPermission(context: android.content.Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

@Composable
fun ScannerRoute(navController: NavHostController) {
    val vm: ScannerViewModel = koinViewModel()
    val context = LocalContext.current
    val app = context.applicationContext as ScribeApplication
    val lifecycleOwner = LocalLifecycleOwner.current

    var granted by rememberSaveable { mutableStateOf(hasCameraPermission(context)) }
    var provider by remember { mutableStateOf<Preview.SurfaceProvider?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    var failedMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result -> granted = result }

    // La lente vive mientras el permiso y el PreviewView existan; al salir de
    // la pantalla la composición se descarta y la cámara se libera sola.
    val lens = remember(lifecycleOwner, provider, granted) {
        if (granted && provider != null) app.createLens(lifecycleOwner, provider!!) else null
    }

    LaunchedEffect(lens) {
        val activeLens = lens ?: return@LaunchedEffect
        failedMessage = null
        activeLens.start().collect { scanState ->
            when (scanState) {
                is ScanState.Success -> {
                    when (val event = vm.onCodex(scanState.codex)) {
                        is ScannerEvent.OpenConfirm -> {
                            navController.navigate(LensRoutes.CONFIRM)
                        }
                        is ScannerEvent.OpenExisting -> {
                            navController.navigate(LensRoutes.detail(event.serial))
                        }
                        is ScannerEvent.OpenGarage -> {
                            // QR del taller: saltamos al tab Taller con la placa
                            // ya depositada en el holder; GarageRoute la consume.
                            navController.navigate(LensRoutes.GARAGE) {
                                popUpTo(LensRoutes.SCANNER) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        is ScannerEvent.Notice -> {
                            notice = event.message
                        }
                        null -> Unit
                    }
                }
                is ScanState.Error -> {
                    failedMessage = scanState.message ?: "Error de análisis"
                }
                else -> Unit
            }
        }
    }

    LaunchedEffect(notice) {
        if (notice != null) {
            delay(2_500)
            notice = null
        }
    }

    DisposableEffect(lens) {
        onDispose { lens?.stop() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (granted) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).also { view ->
                        view.scaleType = PreviewView.ScaleType.FILL_CENTER
                        provider = view.surfaceProvider
                    }
                }
            )
            ScannerOverlay()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Lens",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AnimatedVisibility(visible = failedMessage != null, enter = fadeIn(), exit = fadeOut()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = failedMessage ?: "",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.55f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = vm.state.value.statusText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        notice?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        if (!granted) {
            PermissionCard(
                onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )
        }
    }
}

/** Marco de escaneo con las cuatro esquinas dibujadas, sin imágenes. */
@Composable
private fun ScannerOverlay() {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.08f))
        ) {
            val len = 36.dp.toPx()
            val stroke = 5.dp.toPx()
            val corner = StrokeCap.Round

            fun cornerLine(a: Offset, b: Offset) {
                drawLine(
                    color = accent,
                    start = a,
                    end = b,
                    strokeWidth = stroke,
                    cap = corner
                )
            }

            val left = 0f
            val top = 0f
            val right = size.width
            val bottom = size.height

            cornerLine(Offset(left, top + len), Offset(left, top))
            cornerLine(Offset(left, top), Offset(left + len, top))

            cornerLine(Offset(right - len, top), Offset(right, top))
            cornerLine(Offset(right, top), Offset(right, top + len))

            cornerLine(Offset(left, bottom - len), Offset(left, bottom))
            cornerLine(Offset(left, bottom), Offset(left + len, bottom))

            cornerLine(Offset(right - len, bottom), Offset(right, bottom))
            cornerLine(Offset(right, bottom), Offset(right, bottom - len))
        }
    }
}

@Composable
private fun PermissionCard(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Se necesita la cámara",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Lens usa la cámara para leer códigos QR.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(onClick = onRequest) {
                    Text("Conceder permiso")
                }
            }
        }
    }
}
