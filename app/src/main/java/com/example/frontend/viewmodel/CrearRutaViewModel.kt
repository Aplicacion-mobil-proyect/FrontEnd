package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Recinto
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.session.Sesion
import com.example.frontend.util.Validar
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrearRutaViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val MIN_PARADAS = 2
        const val MAX_PARADAS = 8
        const val MINUTOS_TRASLADO = 15   // tiempo estimado entre una parada y la siguiente
    }

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuarioId: Int? = Sesion.obtener(application)?.id

    val recintos: StateFlow<UiState<List<Recinto>>> = repo.recintos().toUiState(viewModelScope)

    var nombre by mutableStateOf("")
    var errorNombre by mutableStateOf<String?>(null)
    var errorParadas by mutableStateOf<String?>(null)

    /** Ids de los recintos elegidos, en el orden en que se tocaron (ese es el orden de la ruta). */
    val seleccion = mutableStateListOf<Int>()

    var guardando by mutableStateOf(false)
        private set
    var guardado by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    fun cambiarNombre(v: String) { nombre = v.take(40); errorNombre = null }

    fun alternar(recintoId: Int) {
        errorParadas = null
        if (recintoId in seleccion) seleccion.remove(recintoId)
        else if (seleccion.size >= MAX_PARADAS) mensaje = "Máximo $MAX_PARADAS paradas por ruta"
        else seleccion.add(recintoId)
    }

    /** Duración estimada = suma de las visitas + traslado entre paradas. */
    fun duracionEstimada(recintos: List<Recinto>): Int {
        val elegidos = seleccion.mapNotNull { id -> recintos.firstOrNull { it.id == id } }
        if (elegidos.isEmpty()) return 0
        return elegidos.sumOf { it.duracionVisita } + MINUTOS_TRASLADO * (elegidos.size - 1)
    }

    fun guardar(recintos: List<Recinto>) {
        if (guardando) return
        val uid = usuarioId ?: run { mensaje = MSG_SIN_SESION; return }

        errorNombre = Validar.texto(nombre, "un nombre para la ruta", 3, 40)
        errorParadas = if (seleccion.size < MIN_PARADAS) "Elige al menos $MIN_PARADAS recintos" else null
        if (errorNombre != null || errorParadas != null) return

        viewModelScope.launch {
            guardando = true
            repo.crearRuta(uid, nombre, seleccion.toList(), duracionEstimada(recintos))
                .onSuccess { guardado = true }
                .onFailure { mensaje = it.mensajeUsuario() }
            guardando = false
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
