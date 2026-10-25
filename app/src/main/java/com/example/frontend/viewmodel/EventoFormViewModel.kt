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
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.util.FechasUtil
import com.example.frontend.util.Validar
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/** Crear (eventoId = 0) o editar (eventoId > 0) un evento del recinto del encargado. */
class EventoFormViewModel(
    application: Application,
    handle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val recintoId: Int? = recintoIdDelEncargado(application)
    private val eventoId: Int = handle["eventoId"] ?: 0
    val esEdicion = eventoId > 0

    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var tipo by mutableStateOf("Música sacra")
    var fecha by mutableStateOf("")   // dd/MM/aaaa (lo que escribe el usuario)
    var hora by mutableStateOf("")    // HH:mm

    var errorTitulo by mutableStateOf<String?>(null)
    var errorDescripcion by mutableStateOf<String?>(null)
    var errorFecha by mutableStateOf<String?>(null)
    var errorHora by mutableStateOf<String?>(null)

    var cargando by mutableStateOf(esEdicion)
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
        } else if (esEdicion) {
            viewModelScope.launch {
                try {
                    val e = repo.obtenerEvento(eventoId)
                    when {
                        e == null -> errorCarga = "El evento ya no existe"
                        e.recintoId != recintoId -> errorCarga = "No puedes editar eventos de otro recinto"
                        else -> {
                            titulo = e.titulo; descripcion = e.descripcion; tipo = e.tipo
                            fecha = FechasUtil.isoADdMmYyyy(e.fecha); hora = e.hora
                        }
                    }
                } catch (ex: CancellationException) {
                    throw ex
                } catch (ex: Exception) {
                    errorCarga = "No se pudo cargar el evento"
                }
                cargando = false
            }
        }
    }

    fun cambiarTitulo(v: String) { titulo = v.take(80); errorTitulo = null }
    fun cambiarDescripcion(v: String) { descripcion = v.take(500); errorDescripcion = null }
    fun cambiarFecha(v: String) { fecha = v.take(10); errorFecha = null }
    fun cambiarHora(v: String) { hora = v.take(5); errorHora = null }

    fun guardar() {
        val rid = recintoId ?: run { mensaje = MSG_SIN_RECINTO; return }
        if (guardando || errorCarga != null) return

        errorTitulo = Validar.texto(titulo, "un título", 3, 80)
        errorDescripcion = Validar.texto(descripcion, "una descripción", 10, 500)
        val iso = FechasUtil.ddMmYyyyAIso(fecha)
        errorFecha = if (iso == null) "Fecha no válida. Ejemplo: 25/12/2026" else null
        errorHora = if (!FechasUtil.horaValida(hora)) "Hora no válida. Ejemplo: 19:30" else null
        // Un evento nuevo no puede quedar en el pasado
        if (!esEdicion && iso != null && errorHora == null && FechasUtil.yaPaso(iso, hora)) {
            errorFecha = "El evento no puede estar en el pasado"
        }
        if (listOf(errorTitulo, errorDescripcion, errorFecha, errorHora).any { it != null } || iso == null) return

        viewModelScope.launch {
            guardando = true
            repo.guardarEvento(
                Evento(
                    id = if (esEdicion) eventoId else 0,
                    recintoId = rid,
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    tipo = tipo,
                    fecha = iso,
                    hora = hora
                )
            )
                .onSuccess { guardado = true }
                .onFailure { mensaje = it.mensajeUsuario() }
            guardando = false
        }
    }

    fun mensajeMostrado() { mensaje = null }
}
