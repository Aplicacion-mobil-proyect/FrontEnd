package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Recinto
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.util.UbicacionUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

data class PuntoReferencia(val lat: Double, val lng: Double, val esUbicacionReal: Boolean)
data class RecintoCercano(val recinto: Recinto, val distanciaKm: Double)

class CercanosViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())

    // Por defecto se mide desde la Plaza de Armas; el usuario puede cambiarlo a su ubicación real
    private val _referencia = MutableStateFlow(PuntoReferencia(UbicacionUtil.PLAZA_LAT, UbicacionUtil.PLAZA_LNG, false))
    val referencia: StateFlow<PuntoReferencia> = _referencia.asStateFlow()

    var mensaje by mutableStateOf<String?>(null)
        private set

    val uiState: StateFlow<UiState<List<RecintoCercano>>> =
        combine(repo.recintos(), _referencia) { recintos, ref ->
            recintos
                .map { RecintoCercano(it, UbicacionUtil.distanciaKm(ref.lat, ref.lng, it.latitud, it.longitud)) }
                .sortedBy { it.distanciaKm }
        }.toUiState(viewModelScope)

    /** Llamar solo cuando el permiso de ubicación ya fue concedido. */
    fun usarUbicacionReal() {
        val loc = UbicacionUtil.ultimaUbicacion(getApplication())
        if (loc == null) {
            mensaje = "No se pudo obtener tu ubicación (revisa que el GPS esté activo). Usando la Plaza de Armas."
            usarPlazaDeArmas()
        } else {
            _referencia.value = PuntoReferencia(loc.latitude, loc.longitude, true)
        }
    }

    fun usarPlazaDeArmas() {
        _referencia.value = PuntoReferencia(UbicacionUtil.PLAZA_LAT, UbicacionUtil.PLAZA_LNG, false)
    }

    fun permisoDenegado() {
        mensaje = "Sin permiso de ubicación se usa la Plaza de Armas como referencia"
        usarPlazaDeArmas()
    }

    fun mensajeMostrado() { mensaje = null }
}
