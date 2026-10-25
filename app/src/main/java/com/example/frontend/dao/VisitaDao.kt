package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.frontend.model.Visita
import com.example.frontend.model.VisitaDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitaDao {
    @Insert
    suspend fun insertar(visita: Visita)

    @Query("UPDATE visita SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: String)

    // Visitas del usuario con nombre del recinto y del guía (LEFT JOIN porque puede no tener guía)
    @Query(
        "SELECT v.id, r.nombre AS recintoNombre, v.fecha, v.hora, v.personas, v.conGuia, " +
        "g.nombre AS guiaNombre, v.estado " +
        "FROM visita v INNER JOIN recinto r ON r.id = v.recintoId " +
        "LEFT JOIN guia g ON g.id = v.guiaId " +
        "WHERE v.usuarioId = :usuarioId ORDER BY v.fecha, v.hora"
    )
    fun listarPorUsuario(usuarioId: Int): Flow<List<VisitaDetalle>>

    // Ids de guías que ya tienen una visita agendada en esa fecha y hora
    @Query("SELECT guiaId FROM visita WHERE fecha = :fecha AND hora = :hora AND guiaId IS NOT NULL AND estado = 'AGENDADA'")
    suspend fun guiasOcupados(fecha: String, hora: String): List<Int>
}
