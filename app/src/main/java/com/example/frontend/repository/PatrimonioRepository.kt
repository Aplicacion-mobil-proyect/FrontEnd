package com.example.frontend.repository

import com.example.frontend.dao.PatrimonioDao
import com.example.frontend.model.Evento
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.model.EstadosVisita
import com.example.frontend.model.Favorito
import com.example.frontend.model.Guia
import com.example.frontend.model.Horario
import com.example.frontend.model.Visita
import com.example.frontend.model.Ruta
import com.example.frontend.util.FechasUtil
import com.example.frontend.util.TIPOS_EVENTO
import kotlin.coroutines.cancellation.CancellationException

/** Las escrituras devuelven Result con un mensaje claro en caso de error de validación. */
class PatrimonioRepository(private val dao: PatrimonioDao) {

    private inline fun <T> intentar(bloque: () -> T): Result<T> = try {
        Result.success(bloque())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ---- lecturas (Flow: la UI se actualiza sola) ----
    fun eventosDesde(desde: String) = dao.observarEventosDesde(desde)
    fun evento(id: Int) = dao.observarEvento(id)
    fun eventosDeRecinto(recintoId: Int) = dao.observarEventosDeRecinto(recintoId)
    suspend fun obtenerEvento(id: Int) = dao.obtenerEvento(id)
    fun recinto(id: Int) = dao.observarRecinto(id)
    fun horarios(recintoId: Int) = dao.observarHorarios(recintoId)
    suspend fun horariosRegulares(recintoId: Int) = dao.horariosRegulares(recintoId)
    fun favoritos(usuarioId: Int) = dao.observarFavoritos(usuarioId)
    fun idsFavoritos(usuarioId: Int) = dao.observarIdsFavoritos(usuarioId)
    fun visitas(usuarioId: Int) = dao.observarVisitas(usuarioId)

    fun recintos() = dao.observarRecintos()
    fun rutas(usuarioId: Int) = dao.observarRutas(usuarioId)
    suspend fun obtenerRuta(id: Int, usuarioId: Int) = dao.obtenerRuta(id, usuarioId)
    suspend fun paradasDeRuta(rutaId: Int) = dao.paradasDeRuta(rutaId)

    /** Guías que atienden el recinto ese día/hora y que no tienen otra visita a la misma fecha y hora. */
    suspend fun guiasDisponibles(recintoId: Int, fecha: String, dia: Int, hora: String): List<Guia> {
        val ocupados = dao.guiasOcupados(fecha, hora).toSet()
        return dao.guiasEnRecinto(recintoId, dia, hora).filter { it.id !in ocupados }
    }

    // ---- escrituras ----
    suspend fun agendarVisita(visita: Visita): Result<Long> = intentar {
        require(visita.personas in 1..100) { "La cantidad de personas no es válida" }
        require(FechasUtil.horaValida(visita.hora)) { "Hora no válida" }
        require(visita.fecha >= FechasUtil.hoyIso()) { "La fecha no puede ser anterior a hoy" }
        require(!visita.conGuia || visita.guiaId != null) { "Selecciona un guía" }
        dao.agendarVisitaSegura(visita.copy(id = 0, estado = EstadosVisita.AGENDADA, guiaId = if (visita.conGuia) visita.guiaId else null))
    }

    suspend fun crearRuta(usuarioId: Int, nombre: String, recintoIds: List<Int>, duracionMin: Int): Result<Long> = intentar {
        require(nombre.trim().length in 3..40) { "El nombre debe tener entre 3 y 40 caracteres" }
        require(recintoIds.size in 2..8) { "Elige entre 2 y 8 recintos" }
        require(recintoIds.distinct().size == recintoIds.size) { "Hay recintos repetidos" }
        dao.crearRuta(Ruta(usuarioId = usuarioId, nombre = nombre.trim(), duracion = duracionMin), recintoIds)
    }

    suspend fun eliminarRuta(id: Int, usuarioId: Int): Result<Unit> = intentar {
        check(dao.eliminarRutaCompleta(id, usuarioId) > 0) { "La ruta no existe" }
    }

    suspend fun alternarFavorito(usuarioId: Int, recintoId: Int, esFavorito: Boolean): Result<Unit> = intentar {
        if (esFavorito) dao.quitarFavorito(usuarioId, recintoId) else dao.agregarFavorito(Favorito(usuarioId, recintoId))
        Unit
    }

    suspend fun cancelarVisita(id: Int, usuarioId: Int): Result<Unit> = intentar {
        check(dao.actualizarEstadoVisita(id, usuarioId, EstadosVisita.CANCELADA) > 0) { "La visita no existe" }
    }

    suspend fun cambiarEstado(recintoId: Int, estado: String): Result<Unit> = intentar {
        require(estado in EstadosRecinto.todos) { "El estado seleccionado no es válido" }
        check(dao.actualizarEstadoRecinto(recintoId, estado) > 0) { "El recinto ya no existe" }
    }

    suspend fun guardarHorarios(recintoId: Int, horarios: List<Horario>): Result<Unit> = intentar {
        require(horarios.map { it.dia }.distinct().size == horarios.size) { "Hay días repetidos" }
        horarios.forEach {
            require(it.dia in 1..7) { "Día no válido" }
            require(FechasUtil.horaValida(it.apertura) && FechasUtil.horaValida(it.cierre)) { "Hora con formato inválido" }
            require(it.cierre > it.apertura) { "El cierre debe ser posterior a la apertura" }
        }
        dao.reemplazarHorariosRegulares(recintoId, horarios.map { it.copy(id = 0, recintoId = recintoId, especial = false) })
    }

    /** Crea (id = 0) o actualiza un evento. */
    suspend fun guardarEvento(evento: Evento): Result<Unit> = intentar {
        require(evento.titulo.isNotBlank()) { "El título es obligatorio" }
        require(evento.tipo in TIPOS_EVENTO) { "Tipo de evento no válido" }
        require(FechasUtil.horaValida(evento.hora)) { "Hora no válida" }
        if (evento.id == 0) dao.insertarEvento(evento)
        else check(dao.actualizarEvento(evento) > 0) { "El evento ya no existe" }
    }

    suspend fun eliminarEvento(id: Int, recintoId: Int): Result<Unit> = intentar {
        check(dao.eliminarEvento(id, recintoId) > 0) { "No se pudo eliminar: el evento no existe o no es de tu recinto" }
    }
}

fun Throwable.mensajeUsuario(): String = when (this) {
    is IllegalArgumentException, is IllegalStateException -> message ?: "Operación no válida"
    else -> "Ocurrió un error inesperado. Intenta nuevamente."
}
