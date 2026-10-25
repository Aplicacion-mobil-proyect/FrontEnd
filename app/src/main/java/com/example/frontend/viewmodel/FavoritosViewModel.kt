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
import com.example.frontend.session.Sesion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritosViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuarioId: Int? = Sesion.obtener(application)?.id

    var mensaje by mutableStateOf<String?>(null)
        private set

    val uiState: StateFlow<UiState<List<Recinto>>> =
        (if (usuarioId == null) flujoConError<List<Recinto>>(MSG_SIN_SESION) else repo.favoritos(usuarioId))
            .toUiState(viewModelScope)

    fun quitar(recintoId: Int) {
        val uid = usuarioId ?: run { mensaje = MSG_SIN_SESION; return }
        viewModelScope.launch {
            repo.alternarFavorito(uid, recintoId, esFavorito = true).onFailure { mensaje = it.mensajeUsuario() }
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
