package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.frontend.model.Horario
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Insert
    suspend fun insertar(horario: Horario)

    @Insert
    suspend fun insertarTodos(horarios: List<Horario>)

    @Update
    suspend fun actualizar(horario: Horario)

    @Delete
    suspend fun eliminar(horario: Horario)

    @Query("SELECT * FROM horario WHERE recintoId = :recintoId ORDER BY especial, dia, apertura")
    fun listarPorRecinto(recintoId: Int): Flow<List<Horario>>
}
