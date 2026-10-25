package com.example.frontend.repository

import com.example.frontend.dao.RutaDao
import com.example.frontend.model.Recinto
import com.example.frontend.model.Ruta
import com.example.frontend.model.RutaParada
import kotlinx.coroutines.flow.Flow

class RutaRepository(
    private val rutaDao: RutaDao
) {
    fun misRutas(usuarioId: Int): Flow<List<Ruta>> = rutaDao.listarPorUsuario(usuarioId)

    suspend fun buscarPorId(id: Int): Ruta? = rutaDao.buscarPorId(id)

    suspend fun paradas(rutaId: Int): List<Recinto> = rutaDao.listarParadas(rutaId)

    // Guarda la ruta y después cada parada con su orden (0, 1, 2...)
    suspend fun guardar(ruta: Ruta, recintos: List<Recinto>) {
        val rutaId = rutaDao.insertar(ruta).toInt()
        val paradas = recintos.mapIndexed { orden, recinto -> RutaParada(rutaId, orden, recinto.id) }
        rutaDao.insertarParadas(paradas)
    }

    suspend fun eliminar(rutaId: Int) {
        rutaDao.eliminarParadas(rutaId)
        rutaDao.eliminar(rutaId)
    }
}
