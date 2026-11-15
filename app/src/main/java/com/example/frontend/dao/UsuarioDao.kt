package com.example.frontend.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.frontend.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(usuarios: List<Usuario>)

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun buscarPorId(id: Int): Usuario?

    // Flow: el Perfil se actualiza solo cuando se editan los datos
    @Query("SELECT * FROM usuario WHERE id = :id")
    fun observarPorId(id: Int): Flow<Usuario?>

    // ----- Contadores para "Mi actividad" del perfil -----
    @Query("SELECT COUNT(*) FROM favorito WHERE usuarioId = :usuarioId")
    fun contarFavoritos(usuarioId: Int): Flow<Int>

    // Solo las agendadas: las canceladas no cuentan
    @Query("SELECT COUNT(*) FROM visita WHERE usuarioId = :usuarioId AND estado = 'AGENDADA'")
    fun contarVisitas(usuarioId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM ruta WHERE usuarioId = :usuarioId")
    fun contarRutas(usuarioId: Int): Flow<Int>
}
