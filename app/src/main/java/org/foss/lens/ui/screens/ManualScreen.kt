// org/foss/lens/ui/screens/ManualScreen.kt
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
import org.koin.androidx.compose.koinViewModel

/** F4: registro manual de una PC sin QR (teclado táctil). */
@Composable
fun ManualRoute(navController: NavHostController) {
    val vm: ManualAssetViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.saved) {
        if (state.saved) navController.toInventory()
    }

    ScreenColumn {
        TitleRow(title = "Registro manual", onBack = { navController.popBackStack() })

        Text(
            text = "Registra una PC sin necesidad de QR. Se guardará localmente y se enviará al sincronizar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        AssetEditor(
            asset = state.asset,
            onSerial = { v -> vm.updateSerial(v) },
            onUpdate = { transform -> vm.update(transform) },
            serialEnabled = true
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
            Button(
                onClick = { scope.launch { vm.save() } },
                enabled = !state.saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text("Guardar activo")
            }
        }
    }
}
