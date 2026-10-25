package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Recinto
import com.example.frontend.model.Ruta
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.session.Sesion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

data class RutaDetalle(val ruta: Ruta, val paradas: List<Recinto>)

class IniciarRutaViewModel(
    application: Application,
    handle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuarioId: Int? = Sesion.obtener(application)?.id
    private val rutaId: Int = handle["rutaId"] ?: 0

    /** Ids de las paradas ya visitadas (solo mientras la pantalla está abierta). */
    val visitadas = mutableStateListOf<Int>()

    val uiState: StateFlow<UiState<RutaDetalle>> = flow {
        val uid = usuarioId ?: throw IllegalStateException(MSG_SIN_SESION)
        val ruta = repo.obtenerRuta(rutaId, uid) ?: throw NoSuchElementException("La ruta no existe")
        emit(RutaDetalle(ruta, repo.paradasDeRuta(rutaId)))
    }.toUiState(viewModelScope)

    fun alternarVisitada(recintoId: Int) {
        if (recintoId in visitadas) visitadas.remove(recintoId) else visitadas.add(recintoId)
    }
}
