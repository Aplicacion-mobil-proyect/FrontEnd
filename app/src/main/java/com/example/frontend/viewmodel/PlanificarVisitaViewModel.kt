package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Recinto
import com.example.frontend.model.Roles
import com.example.frontend.model.Visita
import com.example.frontend.repository.RecintoRepository
import com.example.frontend.repository.VisitaRepository
import com.example.frontend.session.Sesion
import com.example.frontend.util.FechasUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/*
 * Planificar una visita a un recinto: fecha, hora, cantidad de personas y si quiere guía.
 * Si pide guía, se asigna automáticamente el primer guía disponible en ese recinto, día y hora.
 */
class PlanificarVisitaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val recintoRepository = RecintoRepository(db.recintoDao(), db.horarioDao(), db.favoritoDao())
    private val visitaRepository = VisitaRepository(db.visitaDao(), db.guiaDao())

    private val usuario = Sesion.obtener(application)

    // Un colegio puede llevar un curso completo; un turista, un grupo pequeño
    val maxPersonas = if (usuario?.rol == Roles.COLEGIO) 45 else 10

    // Estado de la pantalla
    var recinto by mutableStateOf<Recinto?>(null)
    var fecha by mutableStateOf("")          // dd/MM/yyyy
    var hora by mutableStateOf("")           // HH:mm
    var personas by mutableStateOf("1")
    var conGuia by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var guardando by mutableStateOf(false)
    var mensajeExito by mutableStateOf<String?>(null)

    fun cargarRecinto(recintoId: Int) {
        viewModelScope.launch {
            recinto = recintoRepository.recinto(recintoId).first()
        }
    }

    fun cambiarFecha(valor: String) { fecha = valor; error = null }
    fun cambiarHora(valor: String) { hora = valor; error = null }
    fun cambiarPersonas(valor: String) { personas = valor.filter { it.isDigit() }.take(2); error = null }
    fun cambiarConGuia(valor: Boolean) { conGuia = valor; error = null }

    fun agendar() {
        val r = recinto ?: return
        val u = usuario ?: run { error = "Debes iniciar sesión"; return }

        // 1. Validaciones del formulario
        val fechaIso = FechasUtil.ddMmYyyyAIso(fecha)
        val cantidad = personas.toIntOrNull()
        when {
            fechaIso == null -> { error = "La fecha debe tener el formato dd/mm/aaaa"; return }
            fechaIso < FechasUtil.hoyIso() -> { error = "La fecha no puede ser anterior a hoy"; return }
            !FechasUtil.horaValida(hora) -> { error = "La hora debe tener el formato HH:mm (ej. 10:30)"; return }
            FechasUtil.yaPaso(fechaIso, hora) -> { error = "Esa hora ya pasó"; return }
            cantidad == null || cantidad < 1 -> { error = "Indica cuántas personas asistirán"; return }
            cantidad > maxPersonas -> { error = "El máximo es $maxPersonas personas"; return }
        }

        guardando = true
        viewModelScope.launch {
            val dia = diaSemana(fechaIso!!)

            // 2. El recinto debe estar abierto ese día a esa hora
            val horarios = recintoRepository.horarios(r.id).first()
            val abierto = horarios.any { !it.especial && it.dia == dia && hora >= it.apertura && hora < it.cierre }
            if (!abierto) {
                error = "El recinto está cerrado el ${FechasUtil.nombreDia(dia).lowercase()} a esa hora"
                guardando = false
                return@launch
            }

            // 3. Si pidió guía, buscar uno libre
            var guiaId: Int? = null
            var guiaNombre: String? = null
            if (conGuia) {
                val guia = visitaRepository.buscarGuiaLibre(r.id, dia, fechaIso, hora)
                if (guia == null) {
                    error = "No hay guías disponibles ese día y hora. Prueba otro horario o sin guía."
                    guardando = false
                    return@launch
                }
                guiaId = guia.id
                guiaNombre = guia.nombre
            }

            // 4. Guardar la visita en Room
            visitaRepository.agendar(
                Visita(
                    usuarioId = u.id,
                    recintoId = r.id,
                    fecha = fechaIso,
                    hora = hora,
                    personas = cantidad!!,
                    conGuia = conGuia,
                    guiaId = guiaId
                )
            )
            guardando = false
            mensajeExito = if (guiaNombre != null) "Visita agendada con el guía $guiaNombre" else "Visita agendada"
        }
    }

    // "2026-11-14" -> 1 (lunes) ... 7 (domingo)
    private fun diaSemana(fechaIso: String): Int {
        val calendario = Calendar.getInstance()
        calendario.time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fechaIso)!!
        // Calendar usa domingo = 1 ... sábado = 7; lo convertimos a lunes = 1 ... domingo = 7
        return (calendario.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
    }
}
