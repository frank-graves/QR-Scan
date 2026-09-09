// org/foss/lens/ui/screens/garage/GarageScreen.kt
package org.foss.lens.ui.screens.garage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.foss.lens.domain.vehicle.Vehicle
import org.foss.lens.ui.components.EmptyHint
import org.foss.lens.ui.components.LensCard
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Presentación de fechas ISO "2025-01-10" → "10/01/2025" (o el texto crudo si algo raro). */
private val READABLE_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun readableDate(iso: String?): String? =
    iso?.let { runCatching { LocalDate.parse(it).format(READABLE_DATE) }.getOrDefault(it) }

/** Tab "Taller" · Talara Motors: registro del cambio de aceite + pendientes. */
@Composable
fun GarageRoute() {
    val vm: GarageViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    var activeTab by rememberSaveable { mutableStateOf(0) }

    // Al volver de la pestaña Escanear puede haber un QR del taller esperando.
    LaunchedEffect(Unit) { vm.consumePendingVehicle() }

    // La escucha de pendientes vive solo mientras su tab está a la vista.
    LaunchedEffect(activeTab) {
        if (activeTab == 1) vm.enterPendingList() else vm.leavePendingList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Taller", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = "Talara Motors · cambio de aceite por QR",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        TabRow(selectedTabIndex = activeTab) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Registrar") }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Pendientes") }
            )
        }
        Spacer(Modifier.height(12.dp))

        when (activeTab) {
            0 -> RegisterPane(
                state = state,
                onLookUp = { raw -> vm.lookUp(raw) },
                onUpdate = { transform -> vm.update(transform) },
                onSave = { vm.saveService() },
                onRetry = { vm.retry() },
                onClearProblem = { vm.clearProblem() }
            )
            else -> PendingPane(
                state = state,
                onAttend = { vehicle ->
                    activeTab = 0
                    vm.lookUp(vehicle.plate)
                },
                onRetry = { vm.retryPendingList() }
            )
        }
    }
}

@Composable
private fun RegisterPane(
    state: GarageUiState,
    onLookUp: (String) -> Unit,
    onUpdate: ((Vehicle) -> Vehicle) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onClearProblem: () -> Unit
) {
    var plateInput by rememberSaveable { mutableStateOf("") }
    val today = remember { LocalDate.now().toString() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = plateInput,
                onValueChange = { plateInput = it.take(140) },
                modifier = Modifier.weight(1f),
                label = { Text("Placa") },
                placeholder = { Text("ABC-123") },
                supportingText = { Text("Escribe la placa o pega el QR") },
                singleLine = true
            )
            Button(
                onClick = { onLookUp(plateInput) },
                enabled = plateInput.isNotBlank(),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(6.dp))
                Text("Buscar")
            }
        }

        Spacer(Modifier.height(12.dp))

        when (state) {
            is GarageUiState.Idle -> IdleHint()
            is GarageUiState.Loading -> CenteredSpinner()
            is GarageUiState.Error -> ErrorPane(reason = state.reason, onRetry = onRetry)
            is GarageUiState.PendingServicesList -> IdleHint()
            is GarageUiState.Success -> ServiceSheet(
                vehicle = state.vehicle,
                today = today,
                justSaved = state.justSaved,
                problem = state.problem,
                onUpdate = onUpdate,
                onSave = onSave,
                onClearProblem = onClearProblem
            )
        }
    }
}

@Composable
private fun IdleHint() {
    LensCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Escanea el QR del vehículo",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Desde la pestaña Escanear, o pega aquí el código del taller. " +
                        "Si la placa no tiene ficha, se abre una en blanco para el primer servicio.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ErrorPane(reason: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reason,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            TextButton(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun CenteredSpinner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/** Ficha del vehículo: datos de mostrador + las dos fechas del servicio. */
@Composable
private fun ServiceSheet(
    vehicle: Vehicle,
    today: String,
    justSaved: Boolean,
    problem: String?,
    onUpdate: ((Vehicle) -> Vehicle) -> Unit,
    onSave: () -> Unit,
    onClearProblem: () -> Unit
) {
    LensCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = vehicle.plate,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = listOfNotNull(vehicle.brand, vehicle.model)
                        .joinToString(" ")
                        .ifBlank { "Vehículo sin ficha previa" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = vehicle.client.orEmpty(),
                onValueChange = { v -> onUpdate { it.copy(client = v.ifBlank { null }) } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cliente") },
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = vehicle.brand.orEmpty(),
                    onValueChange = { v -> onUpdate { it.copy(brand = v.ifBlank { null }) } },
                    modifier = Modifier.weight(1f),
                    label = { Text("Marca") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = vehicle.model.orEmpty(),
                    onValueChange = { v -> onUpdate { it.copy(model = v.ifBlank { null }) } },
                    modifier = Modifier.weight(1f),
                    label = { Text("Modelo") },
                    singleLine = true
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = vehicle.year?.toString().orEmpty(),
                    onValueChange = { v ->
                        val digits = v.filter(Char::isDigit).take(4)
                        onUpdate { it.copy(year = digits.toIntOrNull()) }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Año") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = vehicle.color.orEmpty(),
                    onValueChange = { v -> onUpdate { it.copy(color = v.ifBlank { null }) } },
                    modifier = Modifier.weight(1f),
                    label = { Text("Color") },
                    singleLine = true
                )
            }

            DateField(
                label = "Último cambio",
                value = vehicle.lastServiceDate.orEmpty(),
                hint = "El día en que se hizo el servicio",
                today = today,
                onValue = { v -> onUpdate { it.copy(lastServiceDate = v.ifBlank { null }) } }
            )
            DateField(
                label = "Próximo cambio",
                value = vehicle.nextServiceDate.orEmpty(),
                hint = "Toca la campana de pendientes en esta fecha",
                today = today,
                emphasized = true,
                onValue = { v -> onUpdate { it.copy(nextServiceDate = v.ifBlank { null }) } }
            )

            if (problem != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = problem,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        TextButton(onClick = onClearProblem) {
                            Text("Descartar")
                        }
                    }
                }
            }

            if (justSaved) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Servicio registrado · ${readableDate(vehicle.lastServiceDate)} → ${readableDate(vehicle.nextServiceDate)}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Registrar cambio de aceite", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    hint: String,
    today: String,
    emphasized: Boolean = false,
    onValue: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValue,
            modifier = Modifier.weight(1f),
            label = { Text(label) },
            placeholder = { Text("AAAA-MM-DD") },
            supportingText = { Text(hint) },
            singleLine = true
        )
        TextButton(onClick = { onValue(today) }) {
            Text("Hoy", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun PendingPane(state: GarageUiState, onAttend: (Vehicle) -> Unit, onRetry: () -> Unit) {
    val fleet = (state as? GarageUiState.PendingServicesList)?.vehicles.orEmpty()
    val today = remember { LocalDate.now().toString() }

    // Permisos o red: mostrar el motivo antes que fingir que no hay pendientes.
    if (state is GarageUiState.Error) {
        ErrorPane(reason = state.reason, onRetry = onRetry)
        return
    }

    if (state is GarageUiState.Loading || (state is GarageUiState.Idle && fleet.isEmpty())) {
        CenteredSpinner()
        return
    }

    if (fleet.isEmpty()) {
        EmptyHint(
            title = "Nada pendiente",
            subtitle = "Los vehículos cuyo próximo cambio venció o toca hoy aparecerán aquí."
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(fleet, key = { it.plate }) { vehicle ->
            PendingVehicleRow(vehicle = vehicle, today = today, onAttend = { onAttend(vehicle) })
        }
    }
}

@Composable
private fun PendingVehicleRow(
    vehicle: Vehicle,
    today: String,
    onAttend: () -> Unit
) {
    val next = vehicle.nextServiceDate
    val overdue = next != null && next < today
    val dueLabel = if (overdue) "Venció el ${readableDate(next)}" else "Toca hoy"
    val dueColor = if (overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = vehicle.plate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = listOfNotNull(vehicle.client, vehicle.brand, vehicle.model)
                        .joinToString(" · ")
                        .ifBlank { "Sin datos de ficha" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dueLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = dueColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            TextButton(onClick = onAttend) {
                Text("Atender")
            }
        }
    }
}
