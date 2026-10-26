package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.DisponibilidadGuia
import com.example.frontend.model.Guia
import com.example.frontend.repository.VisitaRepository
import com.example.frontend.util.FechasUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/*
 * Pantalla del ENCARGADO: define en qué días y horas cada guía atiende visitas en su recinto.
 * Estos bloques son los que usa "Planificar visita" para asignar un guía automáticamente.
 */
class GuiasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = VisitaRepository(db.visitaDao(), db.guiaDao())

    // Recinto que administra el encargado logueado (null si no es encargado)
    val recintoId: Int? = recintoIdDelEncargado(application)

    // Listas que se actualizan solas cuando cambia la base de datos (Flow de Room)
    val guias: Flow<List<Guia>> = repository.guias
    val bloques: Flow<List<DisponibilidadGuia>> =
        if (recintoId != null) repository.disponibilidad(recintoId) else emptyFlow()

    // Formulario para agregar un bloque
    var guiaSeleccionada by mutableStateOf<Int?>(null)
    var diaSeleccionado by mutableStateOf(1)
    var inicio by mutableStateOf("")
    var fin by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
    var mensaje by mutableStateOf<String?>(null)

    fun seleccionarGuia(id: Int) { guiaSeleccionada = id; error = null }
    fun seleccionarDia(dia: Int) { diaSeleccionado = dia; error = null }
    fun cambiarInicio(valor: String) { inicio = valor; error = null }
    fun cambiarFin(valor: String) { fin = valor; error = null }

    fun agregar(bloquesActuales: List<DisponibilidadGuia>) {
        val recinto = recintoId ?: run { error = MSG_SIN_RECINTO; return }
        val guia = guiaSeleccionada

        // Validaciones
        when {
            guia == null -> { error = "Selecciona un guía"; return }
            !FechasUtil.horaValida(inicio) || !FechasUtil.horaValida(fin) -> {
                error = "Las horas deben tener el formato HH:mm"; return
            }
            fin <= inicio -> { error = "La hora de término debe ser posterior a la de inicio"; return }
        }

        // No permitir que el mismo guía tenga dos bloques que se crucen el mismo día
        val seCruza = bloquesActuales.any {
            it.guiaId == guia && it.dia == diaSeleccionado && inicio < it.fin && fin > it.inicio
        }
        if (seCruza) { error = "Ese guía ya tiene un bloque que se cruza en ese horario"; return }

        viewModelScope.launch {
            repository.agregarDisponibilidad(
                DisponibilidadGuia(guiaId = guia!!, recintoId = recinto, dia = diaSeleccionado, inicio = inicio, fin = fin)
            )
            inicio = ""
            fin = ""
            mensaje = "Bloque agregado"
        }
    }

    fun eliminar(bloque: DisponibilidadGuia) {
        viewModelScope.launch {
            repository.eliminarDisponibilidad(bloque)
            mensaje = "Bloque eliminado"
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
