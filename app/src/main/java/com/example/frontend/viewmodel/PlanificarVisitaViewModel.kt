package com.example.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.model.Guia
import com.example.frontend.model.Horario
import com.example.frontend.model.Recinto
import com.example.frontend.model.Roles
import com.example.frontend.model.Visita
import com.example.frontend.repository.PatrimonioRepository
import com.example.frontend.repository.mensajeUsuario
import com.example.frontend.session.Sesion
import com.example.frontend.util.FechasUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class PlanificarData(val recinto: Recinto, val horarios: List<Horario>)

class PlanificarVisitaViewModel(
    application: Application,
    handle: SavedStateHandle
) : AndroidViewModel(application) {

    companion object {
        const val MAX_DIAS_ANTICIPACION = 90
    }

    private val repo = PatrimonioRepository(AppDatabase.getDatabase(application).patrimonioDao())
    private val usuario = Sesion.obtener(application)
    private val recintoId: Int = handle["recintoId"] ?: 0

    // Los colegios visitan en grupos grandes; los turistas en grupos chicos
    val minPersonas = if (usuario?.rol == Roles.COLEGIO) 5 else 1
    val maxPersonas = if (usuario?.rol == Roles.COLEGIO) 60 else 20

    // Formulario
    var fecha by mutableStateOf("")      // dd/MM/aaaa
    var hora by mutableStateOf("")       // HH:mm
    var personas by mutableStateOf("")
    var conGuia by mutableStateOf(false)
    var guiaId by mutableStateOf<Int?>(null)

    var errorFecha by mutableStateOf<String?>(null)
    var errorHora by mutableStateOf<String?>(null)
    var errorPersonas by mutableStateOf<String?>(null)
    var errorGuia by mutableStateOf<String?>(null)

    // Guías que pueden atender en la fecha y hora escritas
    var guias by mutableStateOf<List<Guia>>(emptyList())
        private set
    var buscandoGuias by mutableStateOf(false)
        private set

    var guardando by mutableStateOf(false)
        private set
    var agendada by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    val uiState: StateFlow<UiState<PlanificarData>> =
        combine(repo.recinto(recintoId), repo.horarios(recintoId)) { recinto, horarios ->
            PlanificarData(checkNotNull(recinto) { "El recinto no existe" }, horarios)
        }.toUiState(viewModelScope)

    private var jobGuias: Job? = null

    fun cambiarFecha(v: String) { fecha = v.take(10); errorFecha = null; refrescarGuias() }
    fun cambiarHora(v: String) { hora = v.take(5); errorHora = null; refrescarGuias() }
    fun cambiarPersonas(v: String) { personas = v.filter { it.isDigit() }.take(3); errorPersonas = null }
    fun cambiarConGuia(v: Boolean) { conGuia = v; errorGuia = null; guiaId = null; refrescarGuias() }
    fun elegirGuia(id: Int) { guiaId = id; errorGuia = null }

    /** Busca los guías libres cuando la fecha y la hora ya tienen un formato válido. */
    private fun refrescarGuias() {
        jobGuias?.cancel()
        guias = emptyList()
        if (!conGuia) return
        val iso = FechasUtil.ddMmYyyyAIso(fecha)
        if (iso == null || !FechasUtil.horaValida(hora)) return

        jobGuias = viewModelScope.launch {
            buscandoGuias = true
            try {
                guias = repo.guiasDisponibles(recintoId, iso, FechasUtil.diaSemanaIso(iso), hora)
                if (guias.none { it.id == guiaId }) guiaId = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mensaje = "No se pudo consultar la disponibilidad de guías"
            }
            buscandoGuias = false
        }
    }

    fun mensajeMostrado() { mensaje = null }

    fun agendar(data: PlanificarData) {
        if (guardando) return
        val u = usuario ?: run { mensaje = MSG_SIN_SESION; return }
        val recinto = data.recinto
        if (recinto.estado == EstadosRecinto.CERRADO) {
            mensaje = "Este recinto está cerrado: no se pueden agendar visitas"
            return
        }

        // ---- Fecha ----
        val hoy = FechasUtil.hoyIso()
        val iso = FechasUtil.ddMmYyyyAIso(fecha)
        var diaSemana: Int? = null
        errorFecha = when {
            iso == null -> "Fecha no válida. Ejemplo: 25/12/2026"
            iso < hoy -> "La fecha no puede ser anterior a hoy"
            iso > FechasUtil.isoMasDias(MAX_DIAS_ANTICIPACION) -> "Solo puedes agendar con hasta $MAX_DIAS_ANTICIPACION días de anticipación"
            else -> { diaSemana = FechasUtil.diaSemanaIso(iso); null }
        }
        // El recinto debe atender ese día de la semana
        val tramo = diaSemana?.let { d -> data.horarios.firstOrNull { !it.especial && it.dia == d } }
        if (errorFecha == null && tramo == null) {
            errorFecha = "El recinto no atiende ese día (${FechasUtil.nombreDia(diaSemana ?: 1)})"
        }

        // ---- Hora: debe llegar dentro del horario y alcanzar a hacer la visita antes del cierre ----
        errorHora = when {
            !FechasUtil.horaValida(hora) -> "Hora no válida. Ejemplo: 10:30"
            tramo != null -> {
                val inicio = FechasUtil.minutos(tramo.apertura)
                val ultima = FechasUtil.minutos(tramo.cierre) - recinto.duracionVisita
                val m = FechasUtil.minutos(hora)
                when {
                    ultima < inicio -> "El horario de ese día es más corto que la visita (${recinto.duracionVisita} min)"
                    m < inicio || m > ultima ->
                        "Llega entre ${tramo.apertura} y ${FechasUtil.horaDesdeMinutos(ultima)} (la visita dura ${recinto.duracionVisita} min)"
                    iso == hoy && FechasUtil.yaPaso(iso, hora) -> "Esa hora ya pasó"
                    else -> null
                }
            }
            else -> null
        }

        // ---- Personas ----
        val n = personas.trim().toIntOrNull()
        errorPersonas = when {
            n == null -> "Ingresa la cantidad de personas"
            n < minPersonas || n > maxPersonas -> "Debe ser entre $minPersonas y $maxPersonas personas"
            else -> null
        }

        // ---- Guía ----
        errorGuia = when {
            !conGuia -> null
            guiaId != null -> null
            guias.isEmpty() -> "No hay guías disponibles en ese horario. Cambia la hora o desmarca la opción."
            else -> "Selecciona un guía"
        }

        if (listOf(errorFecha, errorHora, errorPersonas, errorGuia).any { it != null } || iso == null || n == null) return

        viewModelScope.launch {
            guardando = true
            repo.agendarVisita(
                Visita(
                    usuarioId = u.id,
                    recintoId = recinto.id,
                    fecha = iso,
                    hora = hora,
                    personas = n,
                    conGuia = conGuia,
                    guiaId = if (conGuia) guiaId else null
                )
            )
                .onSuccess { agendada = true }
                .onFailure {
                    mensaje = it.mensajeUsuario()
                    refrescarGuias() // por si el guía se ocupó mientras llenaba el formulario
                }
            guardando = false
        }
    }
}
