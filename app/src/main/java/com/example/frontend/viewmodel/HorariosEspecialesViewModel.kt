package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Horario
import com.example.frontend.repository.RecintoRepository
import com.example.frontend.util.FechasUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/*
 * Pantalla del ENCARGADO: informa aperturas excepcionales de su recinto
 * (Semana Santa, fiesta patronal, vigilias, etc.).
 * Se guardan en la tabla "horario" con especial = true y se muestran en la ficha del recinto.
 * Para un cierre excepcional, el encargado usa "Cambiar estado" -> CERRADO.
 */
class HorariosEspecialesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = RecintoRepository(db.recintoDao(), db.horarioDao(), db.favoritoDao())

    val recintoId: Int? = recintoIdDelEncargado(application)

    // Solo los horarios especiales del recinto (se actualizan solos)
    val especiales: Flow<List<Horario>> =
        if (recintoId != null) repository.horarios(recintoId).map { lista -> lista.filter { it.especial } }
        else emptyFlow()

    // Formulario
    var dia by mutableStateOf(1)
    var apertura by mutableStateOf("")
    var cierre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
    var mensaje by mutableStateOf<String?>(null)

    fun cambiarDia(valor: Int) { dia = valor; error = null }
    fun cambiarApertura(valor: String) { apertura = valor; error = null }
    fun cambiarCierre(valor: String) { cierre = valor; error = null }
    fun cambiarDescripcion(valor: String) { descripcion = valor.take(60); error = null }

    fun agregar() {
        val recinto = recintoId ?: run { error = MSG_SIN_RECINTO; return }

        // Validaciones
        when {
            descripcion.isBlank() -> { error = "Escribe el motivo (ej. Vigilia de Semana Santa)"; return }
            !FechasUtil.horaValida(apertura) || !FechasUtil.horaValida(cierre) -> {
                error = "Las horas deben tener el formato HH:mm"; return
            }
            cierre <= apertura -> { error = "La hora de cierre debe ser posterior a la de apertura"; return }
        }

        viewModelScope.launch {
            repository.agregarHorario(
                Horario(
                    recintoId = recinto,
                    dia = dia,
                    apertura = apertura,
                    cierre = cierre,
                    especial = true,
                    descripcion = descripcion.trim()
                )
            )
            apertura = ""
            cierre = ""
            descripcion = ""
            mensaje = "Horario especial publicado"
        }
    }

    fun eliminar(horario: Horario) {
        viewModelScope.launch {
            repository.eliminarHorario(horario)
            mensaje = "Horario especial eliminado"
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
