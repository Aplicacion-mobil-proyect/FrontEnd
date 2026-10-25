package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Ruta
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.session.Sesion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MisRutasViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuarioId: Int? = Sesion.obtener(application)?.id

    var mensaje by mutableStateOf<String?>(null)
        private set

    val uiState: StateFlow<UiState<List<Ruta>>> =
        (if (usuarioId == null) flujoConError<List<Ruta>>(MSG_SIN_SESION) else repo.rutas(usuarioId))
            .toUiState(viewModelScope)

    fun eliminar(rutaId: Int) {
        val uid = usuarioId ?: run { mensaje = MSG_SIN_SESION; return }
        viewModelScope.launch {
            repo.eliminarRuta(rutaId, uid)
                .onSuccess { mensaje = "Ruta eliminada" }
                .onFailure { mensaje = it.mensajeUsuario() }
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
