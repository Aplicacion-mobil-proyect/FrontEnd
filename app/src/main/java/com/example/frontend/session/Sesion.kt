package com.example.frontend.session

import android.content.Context
import com.example.frontend.model.Usuario

/*
 * Guarda el usuario que inició sesión en SharedPreferences,
 * así la sesión sigue activa aunque se cierre la app.
 *
 * Uso:  val usuario = Sesion.obtener(context)   // null si nadie inició sesión
 */
object Sesion {

    private const val ARCHIVO = "sesion"

    fun guardar(context: Context, usuario: Usuario) {
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).edit()
            .putInt("id", usuario.id)
            .putString("nombre", usuario.nombre)
            .putString("email", usuario.email)
            .putString("rol", usuario.rol)
            .putInt("recintoId", usuario.recintoId ?: -1)
            .apply()
    }

    fun obtener(context: Context): Usuario? {
        val prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        val id = prefs.getInt("id", -1)
        if (id == -1) return null

        val recintoId = prefs.getInt("recintoId", -1)
        return Usuario(
            id = id,
            nombre = prefs.getString("nombre", "") ?: "",
            email = prefs.getString("email", "") ?: "",
            password = "",                       // la contraseña no se guarda en la sesión
            rol = prefs.getString("rol", "") ?: "",
            recintoId = if (recintoId == -1) null else recintoId
        )
    }

    fun cerrar(context: Context) {
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
