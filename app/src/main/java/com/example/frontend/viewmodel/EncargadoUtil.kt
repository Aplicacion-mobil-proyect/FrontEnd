package com.example.frontend.viewmodel

import android.content.Context
import com.example.frontend.model.Roles
import com.example.frontend.session.Sesion

/** Recinto que administra el usuario logueado, o null si no es ENCARGADO / no tiene recinto asignado. */
fun recintoIdDelEncargado(context: Context): Int? {
    val u = Sesion.obtener(context) ?: return null
    return if (u.rol == Roles.ENCARGADO) u.recintoId else null
}

const val MSG_SIN_RECINTO = "Tu cuenta no tiene un recinto asignado"
const val MSG_SIN_SESION = "Debes iniciar sesión"
