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
import com.example.frontend.repository.mensajeUsuario
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CambiarEstadoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val recintoId: Int? = recintoIdDelEncargado(application)

    var seleccion by mutableStateOf<String?>(null)
        private set
    var guardando by mutableStateOf(false)
        private set
    var guardado by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    val uiState: StateFlow<UiState<Recinto>> =
        (if (recintoId == null) flujoConError<Recinto>(MSG_SIN_RECINTO)
        else repo.recinto(recintoId).map { checkNotNull(it) { "El recinto asignado ya no existe" } })
            .toUiState(viewModelScope)

    fun seleccionar(estado: String) { seleccion = estado }

    fun guardar(estadoActual: String) {
        val id = recintoId ?: run { mensaje = MSG_SIN_RECINTO; return }
        val nuevo = seleccion ?: run { mensaje = "Selecciona un estado"; return }
        if (nuevo == estadoActual) { mensaje = "El recinto ya está en ese estado"; return }
        if (guardando) return
        viewModelScope.launch {
            guardando = true
            repo.cambiarEstado(id, nuevo)
                .onSuccess { guardado = true }
                .onFailure { mensaje = it.mensajeUsuario() }
            guardando = false
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
