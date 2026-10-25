package com.example.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.frontend.model.Usuario
import com.example.frontend.session.Sesion

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    /** Usuario con la sesión iniciada (null si no hay). */
    val usuario: Usuario? = Sesion.obtener(application)

    fun cerrarSesion() = Sesion.cerrar(getApplication())
}
