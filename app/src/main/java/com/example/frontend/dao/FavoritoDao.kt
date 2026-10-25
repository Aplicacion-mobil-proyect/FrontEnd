package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.frontend.model.Favorito
import com.example.frontend.model.Recinto
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregar(favorito: Favorito)

    @Query("DELETE FROM favorito WHERE usuarioId = :usuarioId AND recintoId = :recintoId")
    suspend fun quitar(usuarioId: Int, recintoId: Int)

    // Recintos favoritos de un usuario (JOIN)
    @Query(
        "SELECT r.* FROM recinto r INNER JOIN favorito f ON f.recintoId = r.id " +
        "WHERE f.usuarioId = :usuarioId ORDER BY r.nombre"
    )
    fun listarRecintos(usuarioId: Int): Flow<List<Recinto>>

    @Query("SELECT COUNT(*) > 0 FROM favorito WHERE usuarioId = :usuarioId AND recintoId = :recintoId")
    fun esFavorito(usuarioId: Int, recintoId: Int): Flow<Boolean>
}
