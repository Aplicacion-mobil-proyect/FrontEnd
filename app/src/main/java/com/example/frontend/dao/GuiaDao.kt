package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.frontend.model.DisponibilidadGuia
import com.example.frontend.model.Guia
import kotlinx.coroutines.flow.Flow

@Dao
interface GuiaDao {
    @Insert
    suspend fun insertarTodos(guias: List<Guia>)

    @Insert
    suspend fun insertarDisponibilidad(disponibilidad: DisponibilidadGuia)

    @Insert
    suspend fun insertarDisponibilidades(lista: List<DisponibilidadGuia>)

    @Delete
    suspend fun eliminarDisponibilidad(disponibilidad: DisponibilidadGuia)

    @Query("SELECT * FROM guia ORDER BY nombre")
    fun listar(): Flow<List<Guia>>

    @Query("SELECT * FROM disponibilidad_guia WHERE recintoId = :recintoId ORDER BY dia, inicio")
    fun listarDisponibilidad(recintoId: Int): Flow<List<DisponibilidadGuia>>

    // Guías que atienden ese recinto ese día y cuyo bloque incluye la hora pedida.
    // Las horas "HH:mm" se pueden comparar como texto porque siempre tienen el mismo formato.
    @Query(
        "SELECT g.* FROM guia g INNER JOIN disponibilidad_guia d ON d.guiaId = g.id " +
        "WHERE d.recintoId = :recintoId AND d.dia = :dia AND d.inicio <= :hora AND d.fin > :hora"
    )
    suspend fun disponibles(recintoId: Int, dia: Int, hora: String): List<Guia>
}
