package com.example.frontend.repository

import com.example.frontend.dao.FavoritoDao
import com.example.frontend.dao.HorarioDao
import com.example.frontend.dao.RecintoDao
import com.example.frontend.model.Favorito
import com.example.frontend.model.Horario
import com.example.frontend.model.Recinto
import kotlinx.coroutines.flow.Flow

// Recintos, sus horarios y los favoritos
class RecintoRepository(
    private val recintoDao: RecintoDao,
    private val horarioDao: HorarioDao,
    private val favoritoDao: FavoritoDao
) {
    val recintos: Flow<List<Recinto>> = recintoDao.listar()

    fun recinto(id: Int): Flow<Recinto?> = recintoDao.buscarPorId(id)

    suspend fun cambiarEstado(id: Int, estado: String) = recintoDao.cambiarEstado(id, estado)

    // Horarios
    fun horarios(recintoId: Int): Flow<List<Horario>> = horarioDao.listarPorRecinto(recintoId)
    suspend fun agregarHorario(horario: Horario) = horarioDao.insertar(horario)
    suspend fun actualizarHorario(horario: Horario) = horarioDao.actualizar(horario)
    suspend fun eliminarHorario(horario: Horario) = horarioDao.eliminar(horario)

    // Favoritos
    fun favoritos(usuarioId: Int): Flow<List<Recinto>> = favoritoDao.listarRecintos(usuarioId)
    fun esFavorito(usuarioId: Int, recintoId: Int): Flow<Boolean> = favoritoDao.esFavorito(usuarioId, recintoId)

    suspend fun agregarFavorito(usuarioId: Int, recintoId: Int) =
        favoritoDao.agregar(Favorito(usuarioId, recintoId))

    suspend fun quitarFavorito(usuarioId: Int, recintoId: Int) =
        favoritoDao.quitar(usuarioId, recintoId)
}
