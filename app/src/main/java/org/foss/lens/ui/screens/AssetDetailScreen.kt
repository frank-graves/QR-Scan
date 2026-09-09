// org/foss/lens/ui/screens/AssetDetailScreen.kt
package org.foss.lens.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.foss.lens.domain.Asset
import org.foss.lens.domain.Origen
import org.foss.lens.domain.SyncState
import org.foss.lens.ui.LensRoutes
import org.foss.lens.ui.components.EmptyHint
import org.foss.lens.ui.components.KeyValue
import org.foss.lens.ui.components.LensCard
import org.foss.lens.ui.components.SyncBadge
import org.foss.lens.ui.components.formatDateTime
import org.koin.androidx.compose.koinViewModel

/** Detalle completo de un activo con su estado de sincronización. */
@Composable
fun AssetDetailRoute(navController: NavHostController, serial: String) {
    val vm: AssetDetailViewModel = koinViewModel()
    val asset by vm.asset.collectAsState()
    val busy by vm.busy.collectAsState()
    val notice by vm.notice.collectAsState()

    LaunchedEffect(serial) { vm.bind(serial) }

    ScreenColumn {
        TitleRow(title = "Activo", onBack = { navController.popBackStack() })

        val current = asset
        if (current == null) {
            EmptyHint(title = "Activo no encontrado", subtitle = serial)
            return@ScreenColumn
        }

        SyncBadge(current.syncState, current.errorMsg)
        Spacer(Modifier.height(12.dp))

        LensCard {
            Text("Identificación", style = MaterialTheme.typography.titleSmall)
            KeyValue("Serial", current.serial)
            KeyValue("Marca", current.marca)
            KeyValue("Modelo", current.modelo)
            KeyValue("Tipo", current.tipo)
            KeyValue("Estado", current.estado)
            KeyValue("Ubicación", current.ubicacion)
            KeyValue("Origen", if (current.origen == Origen.QR) "QR" else "Manual")
            KeyValue("Registrado", formatDateTime(current.createdLocalMs))
        }

        LensCard {
            Text("Componentes", style = MaterialTheme.typography.titleSmall)
            KeyValue("CPU", current.componentes.cpu)
            KeyValue("RAM", current.componentes.ramGb?.let { "$it GB" })
            KeyValue("Disco", current.componentes.discoGb?.let { "$it GB" })
            KeyValue("SO", current.componentes.so)
        }

        current.notas?.takeIf { it.isNotBlank() }?.let { notas ->
            LensCard {
                Text("Notas", style = MaterialTheme.typography.titleSmall)
                Text(notas, style = MaterialTheme.typography.bodyMedium)
            }
        }

        notice?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    vm.edit()?.let { navController.navigate(LensRoutes.CONFIRM) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Editar")
            }
            Button(
                onClick = { vm.resend() },
                enabled = !busy && current.syncState != SyncState.SYNCED,
                modifier = Modifier.weight(1f)
            ) {
                if (busy) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text("Reenviar")
            }
        }
    }
}
