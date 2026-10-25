package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Evento
import com.example.frontend.model.Horario
import com.example.frontend.model.Recinto
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.session.Sesion
import com.example.frontend.util.FechasUtil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FichaData(val recinto: Recinto, val horarios: List<Horario>, val eventos: List<Evento>)

class FichaViewModel(
    application: Application,
    handle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuarioId: Int? = Sesion.obtener(application)?.id
    private val recintoId: Int = handle["recintoId"] ?: 0

    var mensaje by mutableStateOf<String?>(null)
        private set

    // Ficha completa: datos del recinto + horarios + eventos que aún no pasan
    val uiState: StateFlow<UiState<FichaData>> = combine(
        repo.recinto(recintoId), repo.horarios(recintoId), repo.eventosDeRecinto(recintoId)
    ) { recinto, horarios, eventos ->
        val hoy = FechasUtil.hoyIso()
        FichaData(
            recinto = checkNotNull(recinto) { "El recinto no existe" },
            horarios = horarios,
            eventos = eventos.filter { it.fecha >= hoy }
        )
    }.toUiState(viewModelScope)

    val esFavorito: StateFlow<Boolean> =
        (if (usuarioId == null) flowOf(emptyList<Int>()) else repo.idsFavoritos(usuarioId))
            .map { recintoId in it }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun alternarFavorito() {
        val uid = usuarioId ?: run { mensaje = MSG_SIN_SESION; return }
        viewModelScope.launch {
            repo.alternarFavorito(uid, recintoId, esFavorito.value).onFailure { mensaje = it.mensajeUsuario() }
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
