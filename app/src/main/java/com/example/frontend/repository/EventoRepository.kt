package com.example.frontend.repository

import com.example.frontend.dao.EventoDao
import com.example.frontend.model.Evento
import com.example.frontend.model.EventoConRecinto
import kotlinx.coroutines.flow.Flow

class EventoRepository(
    private val eventoDao: EventoDao
) {
    val eventos: Flow<List<EventoConRecinto>> = eventoDao.listarConRecinto()

    fun evento(id: Int): Flow<EventoConRecinto?> = eventoDao.buscarConRecinto(id)
    fun eventosDeRecinto(recintoId: Int): Flow<List<Evento>> = eventoDao.listarPorRecinto(recintoId)
    suspend fun buscarPorId(id: Int): Evento? = eventoDao.buscarPorId(id)

    suspend fun insertar(evento: Evento) = eventoDao.insertar(evento)
    suspend fun actualizar(evento: Evento) = eventoDao.actualizar(evento)
    suspend fun eliminar(evento: Evento) = eventoDao.eliminar(evento)
}
