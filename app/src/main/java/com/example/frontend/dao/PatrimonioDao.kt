package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.frontend.model.Evento
import com.example.frontend.model.EventoConRecinto
import com.example.frontend.model.Favorito
import com.example.frontend.model.Horario
import com.example.frontend.model.Recinto
import com.example.frontend.model.Ruta
import com.example.frontend.model.RutaParada
import com.example.frontend.model.Visita
import com.example.frontend.model.VisitaDetalle
import kotlinx.coroutines.flow.Flow

/*
 * DAO con las consultas de: eventos, favoritos, mis visitas y panel del encargado.
 * Es un archivo NUEVO para no tocar los DAOs existentes. Solo hay que registrarlo en AppDatabase:
 *     abstract fun patrimonioDao(): PatrimonioDao
 */
@Dao
abstract class PatrimonioDao {

    // ---------------- Eventos ----------------
    @Query(
        """SELECT e.id AS id, e.recintoId AS recintoId, r.nombre AS recintoNombre, e.titulo AS titulo,
                  e.descripcion AS descripcion, e.tipo AS tipo, e.fecha AS fecha, e.hora AS hora
           FROM evento e INNER JOIN recinto r ON r.id = e.recintoId
           WHERE e.fecha >= :desde ORDER BY e.fecha, e.hora"""
    )
    abstract fun observarEventosDesde(desde: String): Flow<List<EventoConRecinto>>

    @Query(
        """SELECT e.id AS id, e.recintoId AS recintoId, r.nombre AS recintoNombre, e.titulo AS titulo,
                  e.descripcion AS descripcion, e.tipo AS tipo, e.fecha AS fecha, e.hora AS hora
           FROM evento e INNER JOIN recinto r ON r.id = e.recintoId
           WHERE e.id = :id"""
    )
    abstract fun observarEvento(id: Int): Flow<EventoConRecinto?>

    @Query("SELECT * FROM evento WHERE recintoId = :recintoId ORDER BY fecha, hora")
    abstract fun observarEventosDeRecinto(recintoId: Int): Flow<List<Evento>>

    @Query("SELECT * FROM evento WHERE id = :id")
    abstract suspend fun obtenerEvento(id: Int): Evento?

    @Insert
    abstract suspend fun insertarEvento(evento: Evento): Long

    @Update
    abstract suspend fun actualizarEvento(evento: Evento): Int

    // Solo borra si el evento pertenece al recinto del encargado
    @Query("DELETE FROM evento WHERE id = :id AND recintoId = :recintoId")
    abstract suspend fun eliminarEvento(id: Int, recintoId: Int): Int

    // ---------------- Recinto ----------------
    @Query("SELECT * FROM recinto WHERE id = :id")
    abstract fun observarRecinto(id: Int): Flow<Recinto?>

    @Query("UPDATE recinto SET estado = :estado WHERE id = :id")
    abstract suspend fun actualizarEstadoRecinto(id: Int, estado: String): Int

    // ---------------- Horarios ----------------
    @Query("SELECT * FROM horario WHERE recintoId = :recintoId ORDER BY especial, dia, apertura")
    abstract fun observarHorarios(recintoId: Int): Flow<List<Horario>>

    @Query("SELECT * FROM horario WHERE recintoId = :recintoId AND especial = 0 ORDER BY dia")
    abstract suspend fun horariosRegulares(recintoId: Int): List<Horario>

    @Insert
    abstract suspend fun insertarHorarios(horarios: List<Horario>)

    @Query("DELETE FROM horario WHERE recintoId = :recintoId AND especial = 0")
    abstract suspend fun eliminarHorariosRegulares(recintoId: Int)

    /** Reemplaza el horario semanal en una sola transacción (no toca los horarios especiales). */
    @Transaction
    open suspend fun reemplazarHorariosRegulares(recintoId: Int, nuevos: List<Horario>) {
        eliminarHorariosRegulares(recintoId)
        if (nuevos.isNotEmpty()) insertarHorarios(nuevos)
    }

    // ---------------- Favoritos ----------------
    @Query(
        """SELECT r.* FROM recinto r INNER JOIN favorito f ON f.recintoId = r.id
           WHERE f.usuarioId = :usuarioId ORDER BY r.nombre"""
    )
    abstract fun observarFavoritos(usuarioId: Int): Flow<List<Recinto>>

    @Query("SELECT recintoId FROM favorito WHERE usuarioId = :usuarioId")
    abstract fun observarIdsFavoritos(usuarioId: Int): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun agregarFavorito(favorito: Favorito)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun agregarFavoritos(favoritos: List<Favorito>)

    @Query("DELETE FROM favorito WHERE usuarioId = :usuarioId AND recintoId = :recintoId")
    abstract suspend fun quitarFavorito(usuarioId: Int, recintoId: Int): Int

    // ---------------- Visitas ----------------
    @Query(
        """SELECT v.id AS id, r.nombre AS recintoNombre, v.fecha AS fecha, v.hora AS hora,
                  v.personas AS personas, v.conGuia AS conGuia, g.nombre AS guiaNombre, v.estado AS estado
           FROM visita v INNER JOIN recinto r ON r.id = v.recintoId
           LEFT JOIN guia g ON g.id = v.guiaId
           WHERE v.usuarioId = :usuarioId ORDER BY v.fecha DESC, v.hora DESC"""
    )
    abstract fun observarVisitas(usuarioId: Int): Flow<List<VisitaDetalle>>

    // Solo cambia visitas del propio usuario
    @Query("UPDATE visita SET estado = :estado WHERE id = :id AND usuarioId = :usuarioId")
    abstract suspend fun actualizarEstadoVisita(id: Int, usuarioId: Int, estado: String): Int

    @Insert
    abstract suspend fun insertarVisitas(visitas: List<Visita>)

    // ---------------- Recintos (listado completo) ----------------
    @Query("SELECT * FROM recinto ORDER BY nombre")
    abstract fun observarRecintos(): Flow<List<Recinto>>

    // ---------------- Rutas del usuario ----------------
    @Query("SELECT * FROM ruta WHERE usuarioId = :usuarioId ORDER BY id DESC")
    abstract fun observarRutas(usuarioId: Int): Flow<List<Ruta>>

    // Solo devuelve la ruta si pertenece al usuario
    @Query("SELECT * FROM ruta WHERE id = :id AND usuarioId = :usuarioId")
    abstract suspend fun obtenerRuta(id: Int, usuarioId: Int): Ruta?

    // Recintos de la ruta en el orden de las paradas
    @Query(
        """SELECT r.* FROM recinto r INNER JOIN ruta_parada p ON p.recintoId = r.id
           WHERE p.rutaId = :rutaId ORDER BY p.orden"""
    )
    abstract suspend fun paradasDeRuta(rutaId: Int): List<Recinto>

    @Insert
    abstract suspend fun insertarRuta(ruta: Ruta): Long

    @Insert
    abstract suspend fun insertarParadas(paradas: List<RutaParada>)

    @Query("DELETE FROM ruta WHERE id = :id AND usuarioId = :usuarioId")
    abstract suspend fun eliminarRuta(id: Int, usuarioId: Int): Int

    @Query("DELETE FROM ruta_parada WHERE rutaId = :rutaId")
    abstract suspend fun eliminarParadas(rutaId: Int)

    /** Crea la ruta y sus paradas en una sola transacción (o se guarda todo, o nada). */
    @Transaction
    open suspend fun crearRuta(ruta: Ruta, recintoIds: List<Int>): Long {
        val id = insertarRuta(ruta.copy(id = 0))
        insertarParadas(recintoIds.mapIndexed { i, recintoId -> RutaParada(id.toInt(), i + 1, recintoId) })
        return id
    }

    @Transaction
    open suspend fun eliminarRutaCompleta(id: Int, usuarioId: Int): Int {
        val borradas = eliminarRuta(id, usuarioId)
        if (borradas > 0) eliminarParadas(id)
        return borradas
    }
}
