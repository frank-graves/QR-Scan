// org/foss/lens/ui/screens/ConfirmScreen.kt
package org.foss.lens.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.foss.lens.ui.components.EmptyHint
import org.koin.androidx.compose.koinViewModel

/**
 * F3: antes de enviar nada al backend, el usuario ve la información parseada
 * del QR y decide: editar, confirmar (guardar + encolar sync) o descartar.
 */
@Composable
fun ConfirmRoute(navController: NavHostController) {
    val vm: ConfirmAssetViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val asset = state.asset

    LaunchedEffect(state.saved) {
        if (state.saved) navController.toInventory()
    }

    ScreenColumn {
        TitleRow(title = "Confirmar activo", onBack = { navController.popBackStack() })

        if (asset == null) {
            EmptyHint(
                title = "No hay un escaneo pendiente",
                subtitle = "Vuelve al escáner y apunta a un QR de inventario."
            )
            return@ScreenColumn
        }

        Text(
            text = "Revisa los datos antes de guardar. Se enviarán a la base cuando haya conexión.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        AssetEditor(
            asset = asset,
            onSerial = { v -> vm.updateSerial(v) },
            onUpdate = { transform -> vm.update(transform) }
        )

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Descartar")
            }
            Button(
                onClick = { scope.launch { vm.save() } },
                enabled = !state.saving,
                modifier = Modifier.weight(2f)
            ) {
                if (state.saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text("Guardar y enviar")
            }
        }
    }
}
