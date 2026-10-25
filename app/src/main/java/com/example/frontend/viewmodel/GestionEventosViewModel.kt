package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Evento
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GestionEventosViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val recintoId: Int? = recintoIdDelEncargado(application)

    var mensaje by mutableStateOf<String?>(null)
        private set

    val uiState: StateFlow<UiState<List<Evento>>> =
        (if (recintoId == null) flujoConError<List<Evento>>(MSG_SIN_RECINTO) else repo.eventosDeRecinto(recintoId))
            .toUiState(viewModelScope)

    fun eliminar(eventoId: Int) {
        val id = recintoId ?: run { mensaje = MSG_SIN_RECINTO; return }
        viewModelScope.launch {
            repo.eliminarEvento(eventoId, id)
                .onSuccess { mensaje = "Evento eliminado" }
                .onFailure { mensaje = it.mensajeUsuario() }
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
