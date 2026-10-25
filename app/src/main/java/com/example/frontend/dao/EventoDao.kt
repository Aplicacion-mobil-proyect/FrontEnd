package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room3.Insert
import com.example.frontend.model.Evento
import com.example.frontend.model.EventoConRecinto
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    @Insert
    suspend fun insertar(evento: Evento)

    @Insert
    suspend fun insertarTodos(eventos: List<Evento>)

    @Update
    suspend fun actualizar(evento: Evento)

    @Delete
    suspend fun eliminar(evento: Evento)

    // Todos los eventos con el nombre de su recinto (JOIN)
    @Query(
        "SELECT e.id, e.recintoId, r.nombre AS recintoNombre, e.titulo, e.descripcion, e.tipo, e.fecha, e.hora " +
        "FROM evento e INNER JOIN recinto r ON r.id = e.recintoId ORDER BY e.fecha, e.hora"
    )
    fun listarConRecinto(): Flow<List<EventoConRecinto>>

    @Query(
        "SELECT e.id, e.recintoId, r.nombre AS recintoNombre, e.titulo, e.descripcion, e.tipo, e.fecha, e.hora " +
        "FROM evento e INNER JOIN recinto r ON r.id = e.recintoId WHERE e.id = :id"
    )
    fun buscarConRecinto(id: Int): Flow<EventoConRecinto?>

    @Query("SELECT * FROM evento WHERE recintoId = :recintoId ORDER BY fecha, hora")
    fun listarPorRecinto(recintoId: Int): Flow<List<Evento>>

    @Query("SELECT * FROM evento WHERE id = :id")
    suspend fun buscarPorId(id: Int): Evento?
}
