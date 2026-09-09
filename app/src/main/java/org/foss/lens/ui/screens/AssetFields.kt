// org/foss/lens/ui/screens/AssetFields.kt
package org.foss.lens.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.foss.lens.domain.Asset
import org.foss.lens.domain.AssetContract
import org.foss.lens.ui.components.LensCard

/**
 * Formulario de activo compartido por la pantalla de confirmación (post-escaneo)
 * y el registro manual. La pantalla decide qué hacer con el resultado; aquí solo
 * se editan campos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetEditor(
    asset: Asset,
    onSerial: (String) -> Unit,
    onUpdate: ((Asset) -> Asset) -> Unit,
    serialEnabled: Boolean = true
) {
    LensCard {
        Text("Datos generales", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = asset.serial,
            onValueChange = onSerial,
            label = { Text("Serial *") },
            enabled = serialEnabled,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = asset.marca ?: "",
            onValueChange = { v -> onUpdate { it.copy(marca = v.ifBlank { null }) } },
            label = { Text("Marca") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = asset.modelo ?: "",
            onValueChange = { v -> onUpdate { it.copy(modelo = v.ifBlank { null }) } },
            label = { Text("Modelo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = asset.ubicacion ?: "",
            onValueChange = { v -> onUpdate { it.copy(ubicacion = v.ifBlank { null }) } },
            label = { Text("Ubicación") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }

    LensCard {
        Text("Clasificación", style = MaterialTheme.typography.titleSmall)
        Text("Tipo", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssetContract.tipos.forEach { tipo ->
                FilterChip(
                    selected = asset.tipo == tipo,
                    onClick = { onUpdate { it.copy(tipo = tipo) } },
                    label = { Text(tipo) }
                )
            }
        }
        Text("Estado", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssetContract.estados.forEach { estado ->
                FilterChip(
                    selected = asset.estado == estado,
                    onClick = { onUpdate { it.copy(estado = estado) } },
                    label = { Text(estado) }
                )
            }
        }
    }

    LensCard {
        Text("Componentes", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = asset.componentes.cpu ?: "",
            onValueChange = { v ->
                onUpdate { it.copy(componentes = it.componentes.copy(cpu = v.ifBlank { null })) }
            },
            label = { Text("CPU") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = asset.componentes.so ?: "",
            onValueChange = { v ->
                onUpdate { it.copy(componentes = it.componentes.copy(so = v.ifBlank { null })) }
            },
            label = { Text("Sistema operativo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = asset.componentes.ramGb?.toString() ?: "",
            onValueChange = { v ->
                onUpdate { it.copy(componentes = it.componentes.copy(ramGb = v.toIntOrNull())) }
            },
            label = { Text("RAM (GB)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = asset.componentes.discoGb?.toString() ?: "",
            onValueChange = { v ->
                onUpdate { it.copy(componentes = it.componentes.copy(discoGb = v.toIntOrNull())) }
            },
            label = { Text("Disco (GB)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = asset.notas ?: "",
            onValueChange = { v -> onUpdate { it.copy(notas = v.ifBlank { null }) } },
            label = { Text("Notas") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
