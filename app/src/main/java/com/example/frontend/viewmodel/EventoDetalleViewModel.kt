package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.EventoConRecinto
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.session.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventoDetalleViewModel(
    application: Application,
    handle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuarioId: Int? = Sesion.obtener(application)?.id
    private val eventoId: Int = handle["eventoId"] ?: 0

    var mensaje by mutableStateOf<String?>(null)
        private set

    private val eventoFlow = repo.evento(eventoId)

    val uiState: StateFlow<UiState<EventoConRecinto>> = eventoFlow
        .map { it ?: throw NoSuchElementException("El evento ya no existe") }
        .toUiState(viewModelScope)

    private val idsFavoritos = if (usuarioId == null) flowOf(emptyList<Int>()) else repo.idsFavoritos(usuarioId)

    /** true si el recinto del evento está en los favoritos del usuario. */
    val esFavorito: StateFlow<Boolean> = combine(eventoFlow, idsFavoritos) { e, ids -> e != null && e.recintoId in ids }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun alternarFavorito(recintoId: Int) {
        val uid = usuarioId ?: run { mensaje = MSG_SIN_SESION; return }
        viewModelScope.launch {
            repo.alternarFavorito(uid, recintoId, esFavorito.value).onFailure { mensaje = it.mensajeUsuario() }
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
