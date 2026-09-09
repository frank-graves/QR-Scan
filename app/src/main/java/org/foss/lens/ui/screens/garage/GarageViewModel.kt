// org/foss/lens/ui/screens/garage/GarageViewModel.kt
package org.foss.lens.ui.screens.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.foss.lens.domain.vehicle.Vehicle
import org.foss.lens.domain.vehicle.VehicleCodec
import org.foss.lens.domain.vehicle.VehicleRepository
import org.foss.lens.domain.vehicle.isBarePlateScan
import org.foss.lens.observability.AppLogger
import org.foss.lens.ui.PendingScanHolder

/** Lo que el panel del taller puede estar mostrando en cada momento. */
sealed interface GarageUiState {
    /** Sin placa consultada todavía. */
    data object Idle : GarageUiState

    /** Consultando la ficha en Firestore. */
    data object Loading : GarageUiState

    /** Ficha en pantalla lista para editar/guardar el servicio. */
    data class Success(
        val vehicle: Vehicle,
        /** El último guardado terminó bien: la UI muestra el aviso una vez. */
        val justSaved: Boolean = false,
        /** Fallo del guardado, sin tirar abajo la ficha que se estaba editando. */
        val problem: String? = null
    ) : GarageUiState

    /** La consulta falló (sin red, reglas, contenido que no es una placa…). */
    data class Error(val reason: String) : GarageUiState

    /** El tab de pendientes está abierto: lista en vivo de vehículos a atender. */
    data class PendingServicesList(val vehicles: List<Vehicle>) : GarageUiState
}

class GarageViewModel(
    private val garage: VehicleRepository,
    private val vehicleCodec: VehicleCodec,
    private val pendingScan: PendingScanHolder
) : ViewModel() {

    private val _state = MutableStateFlow<GarageUiState>(GarageUiState.Idle)
    val state: StateFlow<GarageUiState> = _state.asStateFlow()

    private var lastPlate: String? = null
    private var pendingJob: Job? = null

    init {
        // Llegamos aquí porque el escáner reconoció un QR del taller: la ficha
        // decodificada ya está en el holder y la mostramos sin pedirle nada.
        consumePendingVehicle()
    }

    /** Consume la ficha que el escáner dejó esperando y la presenta. */
    fun consumePendingVehicle() {
        pendingScan.consumeVehicle()?.let { scanned -> present(scanned) }
    }

    /**
     * Busca por placa o QR pegado. El campo de la UI acepta ambos: si el
     * contenido trae ficha completa, autocompleta; si solo es placa, consulta
     * Firestore por si el vehículo ya tiene historia.
     */
    fun lookUp(rawPlate: String) {
        val scanned = vehicleCodec.decode(rawPlate)
        if (scanned == null) {
            _state.value = GarageUiState.Error("No se reconoce una placa ahí (ej.: ABC-123).")
            return
        }
        present(scanned)
    }

    /**
     * Presenta lo que trajo el QR. Si solo trae placa (texto pelado o JSON
     * mínimo), mejor preguntar al servidor: una placa desnuda no debe pisar
     * una ficha que Firestore ya conoce con más datos.
     */
    private fun present(scanned: Vehicle) {
        lastPlate = scanned.plate
        if (scanned.isBarePlateScan()) {
            fetchFromServer(scanned.plate)
        } else {
            // QR completo: autocompletar al instante; el mecánico solo revisa.
            _state.value = GarageUiState.Success(scanned)
        }
    }

    private fun fetchFromServer(plate: String) {
        _state.value = GarageUiState.Loading
        viewModelScope.launch {
            try {
                val found = garage.findVehicleByPlate(plate)
                // Sin ficha previa abrimos una en blanco: primer servicio.
                _state.value = GarageUiState.Success(found ?: Vehicle(plate = plate))
            } catch (boom: CancellationException) {
                // Se salió de la pantalla mientras Firestore respondía: la
                // cancelación es una orden, no un fallo de red que avisar.
                throw boom
            } catch (boom: Exception) {
                AppLogger.error(TAG, "Consulta fallida para $plate", boom)
                _state.value = GarageUiState.Error("Sin respuesta del taller: ${boom.message}")
            }
        }
    }

    fun retry() {
        lastPlate?.let { fetchFromServer(it) }
    }

    /** Edición en vivo de la ficha mostrada (el form llama con `copy`). */
    fun update(transform: (Vehicle) -> Vehicle) {
        val current = _state.value as? GarageUiState.Success ?: return
        _state.value = current.copy(
            vehicle = transform(current.vehicle),
            justSaved = false,
            problem = null
        )
    }

    fun clearProblem() {
        val current = _state.value as? GarageUiState.Success ?: return
        _state.value = current.copy(problem = null)
    }

    /**
     * Registra el servicio. La próxima fecha es la que dispara la lista de
     * pendientes, así que sin ella no hay guardado que valga.
     */
    fun saveService() {
        val current = _state.value as? GarageUiState.Success ?: return
        val vehicle = current.vehicle
        val next = vehicle.nextServiceDate
        val last = vehicle.lastServiceDate

        if (next == null || !ISO_DATE.matches(next)) {
            _state.value = current.copy(problem = "Falta la próxima fecha del servicio (AAAA-MM-DD).")
            return
        }
        if (last != null && !ISO_DATE.matches(last)) {
            _state.value = current.copy(problem = "La última fecha no tiene formato AAAA-MM-DD.")
            return
        }

        viewModelScope.launch {
            garage.saveService(vehicle)
                .onSuccess {
                    _state.value = current.copy(justSaved = true, problem = null)
                }
                .onFailure { boom ->
                    AppLogger.error(TAG, "No se registró el servicio de ${vehicle.plate}", boom)
                    // La ficha editada se conserva para reintentar sin teclear todo.
                    _state.value = current.copy(problem = "No se guardó: ${boom.message}")
                }
        }
    }

    /** Abre el tab de pendientes y lo mantiene vivo mientras esté visible. */
    fun enterPendingList() {
        pendingJob?.cancel()
        pendingJob = viewModelScope.launch {
            try {
                garage.getPendingVehicles().collect { fleet ->
                    _state.value = GarageUiState.PendingServicesList(fleet)
                }
            } catch (boom: CancellationException) {
                // leavePendingList() cortó la escucha al cambiar de tab, o una
                // re-entrada canceló la escucha anterior: eso es navegación,
                // no un problema del taller. Pintarlo aquí dejaba un error
                // fantasma ("StandaloneCoroutine was cancelled") en Registrar.
                throw boom
            } catch (boom: Exception) {
                // Reglas de seguridad (PERMISSION_DENIED) o red caída: mejor un
                // aviso visible que una lista vacía que parezca "todo al día".
                AppLogger.error(TAG, "Lista de pendientes caída", boom)
                _state.value = GarageUiState.Error("No se pudo leer los pendientes: ${boom.message}")
            }
        }
    }

    /** Reintenta la escucha de pendientes tras un fallo de permisos o de red. */
    fun retryPendingList() = enterPendingList()

    /** Sale del tab de pendientes: corta la escucha y vuelve al registro. */
    fun leavePendingList() {
        pendingJob?.cancel()
        pendingJob = null
        if (_state.value is GarageUiState.PendingServicesList) {
            _state.value = GarageUiState.Idle
        }
    }

    override fun onCleared() {
        pendingJob?.cancel()
    }

    private companion object {
        const val TAG = "GarageVM"
        val ISO_DATE = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    }
}
