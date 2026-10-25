package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Horario
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.util.FechasUtil
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class FilaHorario(
    val dia: Int,
    val abierto: Boolean,
    val apertura: String,
    val cierre: String,
    val error: String? = null
)

/** Edita el horario semanal (un tramo por día). Los horarios especiales no se modifican. */
class EditarHorariosViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val recintoId: Int? = recintoIdDelEncargado(application)

    val filas = mutableStateListOf<FilaHorario>()
    var cargando by mutableStateOf(true)
        private set
    var errorCarga by mutableStateOf<String?>(null)
        private set
    var guardando by mutableStateOf(false)
        private set
    var guardado by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    init {
        if (recintoId == null) {
            errorCarga = MSG_SIN_RECINTO
            cargando = false
        } else {
            viewModelScope.launch {
                try {
                    val regulares = repo.horariosRegulares(recintoId).associateBy { it.dia }
                    filas.addAll((1..7).map { d ->
                        regulares[d]?.let { FilaHorario(d, true, it.apertura, it.cierre) }
                            ?: FilaHorario(d, false, "09:00", "18:00")
                    })
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    errorCarga = "No se pudo cargar el horario"
                }
                cargando = false
            }
        }
    }

    private fun modificar(indice: Int, cambio: (FilaHorario) -> FilaHorario) {
        filas[indice] = cambio(filas[indice]).copy(error = null)
    }

    fun onAbierto(i: Int, v: Boolean) = modificar(i) { it.copy(abierto = v) }
    fun onApertura(i: Int, v: String) = modificar(i) { it.copy(apertura = v.take(5)) }
    fun onCierre(i: Int, v: String) = modificar(i) { it.copy(cierre = v.take(5)) }

    fun guardar() {
        val id = recintoId ?: run { mensaje = MSG_SIN_RECINTO; return }
        if (guardando) return

        // Validación: marca el error en cada fila incorrecta
        var hayErrores = false
        filas.indices.forEach { i ->
            val f = filas[i]
            val error = when {
                !f.abierto -> null
                !FechasUtil.horaValida(f.apertura) || !FechasUtil.horaValida(f.cierre) -> "Usa el formato HH:mm (ej. 09:30)"
                f.cierre <= f.apertura -> "El cierre debe ser posterior a la apertura"
                else -> null
            }
            if (error != null) hayErrores = true
            filas[i] = f.copy(error = error)
        }
        if (hayErrores) { mensaje = "Corrige los horarios marcados en rojo"; return }

        val nuevos = filas.filter { it.abierto }.map { Horario(recintoId = id, dia = it.dia, apertura = it.apertura, cierre = it.cierre) }
        viewModelScope.launch {
            guardando = true
            repo.guardarHorarios(id, nuevos)
                .onSuccess { guardado = true }
                .onFailure { mensaje = it.mensajeUsuario() }
            guardando = false
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
