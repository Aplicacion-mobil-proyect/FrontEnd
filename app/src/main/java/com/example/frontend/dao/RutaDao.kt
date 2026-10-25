package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.frontend.model.Recinto
import com.example.frontend.model.Ruta
import com.example.frontend.model.RutaParada
import kotlinx.coroutines.flow.Flow

@Dao
interface RutaDao {
    @Insert
    suspend fun insertar(ruta: Ruta): Long      // devuelve el id generado

    @Insert
    suspend fun insertarParadas(paradas: List<RutaParada>)

    @Query("DELETE FROM ruta WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("DELETE FROM ruta_parada WHERE rutaId = :rutaId")
    suspend fun eliminarParadas(rutaId: Int)

    @Query("SELECT * FROM ruta WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun listarPorUsuario(usuarioId: Int): Flow<List<Ruta>>

    @Query("SELECT * FROM ruta WHERE id = :id")
    suspend fun buscarPorId(id: Int): Ruta?

    // Recintos de la ruta en el orden de las paradas
    @Query(
        "SELECT r.* FROM recinto r INNER JOIN ruta_parada p ON p.recintoId = r.id " +
        "WHERE p.rutaId = :rutaId ORDER BY p.orden"
    )
    suspend fun listarParadas(rutaId: Int): List<Recinto>
}
