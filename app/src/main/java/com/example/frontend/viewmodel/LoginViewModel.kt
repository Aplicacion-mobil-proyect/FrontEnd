package com.example.frontend.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.R
import com.example.frontend.database.AppDatabase
import com.example.frontend.database.SeedData
import com.example.frontend.repository.UsuarioRepository
import com.example.frontend.session.Sesion
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = UsuarioRepository(db.usuarioDao())

    // Estado de la pantalla (Compose se redibuja cuando cambian)
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var error by mutableStateOf<Int?>(null)    // id del texto (R.string...) para que se traduzca
    var cargando by mutableStateOf(false)
    var loginExitoso by mutableStateOf(false)

    fun cambiarEmail(valor: String) {
        email = valor
        error = null
    }

    fun cambiarPassword(valor: String) {
        password = valor
        error = null
    }

    fun login() {
        // 1. Validaciones del formulario
        if (email.isBlank() || password.isBlank()) {
            error = R.string.error_login_vacio
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            error = R.string.error_correo_formato
            return
        }

        // 2. Consulta a la base de datos (en una corrutina, fuera del hilo principal)
        cargando = true
        viewModelScope.launch {
            try {
                SeedData.cargarSiEstaVacia(db)          // asegura que existan los usuarios de prueba
                val usuario = repository.login(email, password)
                if (usuario != null) {
                    Sesion.guardar(getApplication(), usuario)
                    loginExitoso = true
                } else {
                    error = R.string.error_login_incorrecto
                }
            } catch (e: Exception) {
                error = R.string.error_guardar          // falló la base de datos
            } finally {
                cargando = false                        // pase lo que pase, el botón vuelve a habilitarse
            }
        }
    }
}
