package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.VisitaDetalle
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.session.Sesion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MisVisitasViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuarioId: Int? = Sesion.obtener(application)?.id

    var mensaje by mutableStateOf<String?>(null)
        private set

    val uiState: StateFlow<UiState<List<VisitaDetalle>>> =
        (if (usuarioId == null) flujoConError<List<VisitaDetalle>>(MSG_SIN_SESION) else repo.visitas(usuarioId))
            .toUiState(viewModelScope)

    fun cancelar(visitaId: Int) {
        val uid = usuarioId ?: run { mensaje = MSG_SIN_SESION; return }
        viewModelScope.launch {
            repo.cancelarVisita(visitaId, uid)
                .onSuccess { mensaje = "Visita cancelada" }
                .onFailure { mensaje = it.mensajeUsuario() }
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
