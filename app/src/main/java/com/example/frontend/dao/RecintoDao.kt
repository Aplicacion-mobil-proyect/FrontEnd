package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.frontend.model.Recinto
import kotlinx.coroutines.flow.Flow

@Dao
interface RecintoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(recintos: List<Recinto>)

    @Query("SELECT * FROM recinto ORDER BY nombre")
    fun listar(): Flow<List<Recinto>>

    @Query("SELECT * FROM recinto WHERE id = :id")
    fun buscarPorId(id: Int): Flow<Recinto?>

    @Query("UPDATE recinto SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: String)

    @Query("SELECT COUNT(*) FROM recinto")
    suspend fun contar(): Int
}
