package com.example.frontend.repository

import com.example.frontend.dao.UsuarioDao
import com.example.frontend.model.Usuario

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {
    // Devuelve el usuario si el correo y la contraseña coinciden; si no, null
    suspend fun login(email: String, password: String): Usuario? {
        val usuario = usuarioDao.buscarPorEmail(email.trim().lowercase())
        return if (usuario != null && usuario.password == password) usuario else null
    }

    suspend fun existeEmail(email: String): Boolean =
        usuarioDao.buscarPorEmail(email.trim().lowercase()) != null

    suspend fun registrar(usuario: Usuario) {
        usuarioDao.insertar(usuario.copy(email = usuario.email.trim().lowercase()))
    }

    suspend fun buscarPorId(id: Int): Usuario? = usuarioDao.buscarPorId(id)

    suspend fun actualizar(usuario: Usuario) = usuarioDao.actualizar(usuario)
}
