package com.example.frontend.repository

import com.example.frontend.dao.GuiaDao
import com.example.frontend.dao.VisitaDao
import com.example.frontend.model.DisponibilidadGuia
import com.example.frontend.model.EstadosVisita
import com.example.frontend.model.Guia
import com.example.frontend.model.Visita
import com.example.frontend.model.VisitaDetalle
import kotlinx.coroutines.flow.Flow

// Visitas agendadas y guías
class VisitaRepository(
    private val visitaDao: VisitaDao,
    private val guiaDao: GuiaDao
) {
    fun misVisitas(usuarioId: Int): Flow<List<VisitaDetalle>> = visitaDao.listarPorUsuario(usuarioId)

    suspend fun cancelar(visitaId: Int) = visitaDao.cambiarEstado(visitaId, EstadosVisita.CANCELADA)

    // Busca el primer guía disponible en ese recinto, día y hora que no esté ocupado.
    // Devuelve null si no hay ninguno libre.
    suspend fun buscarGuiaLibre(recintoId: Int, dia: Int, fecha: String, hora: String): Guia? {
        val ocupados = visitaDao.guiasOcupados(fecha, hora)
        val disponibles = guiaDao.disponibles(recintoId, dia, hora)
        return disponibles.firstOrNull { it.id !in ocupados }
    }

    suspend fun agendar(visita: Visita) = visitaDao.insertar(visita)

    // Guías (pantalla del encargado)
    val guias: Flow<List<Guia>> = guiaDao.listar()
    fun disponibilidad(recintoId: Int): Flow<List<DisponibilidadGuia>> = guiaDao.listarDisponibilidad(recintoId)
    suspend fun agregarDisponibilidad(d: DisponibilidadGuia) = guiaDao.insertarDisponibilidad(d)
    suspend fun eliminarDisponibilidad(d: DisponibilidadGuia) = guiaDao.eliminarDisponibilidad(d)
}
