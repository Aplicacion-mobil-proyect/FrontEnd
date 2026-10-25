package com.example.frontend.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.database.SeedData
import com.example.frontend.model.Roles
import com.example.frontend.model.Usuario
import com.example.frontend.session.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    // Campos del formulario
    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmar by mutableStateOf("")
    var rol by mutableStateOf(Roles.TURISTA)

    // Errores por campo
    var errorNombre by mutableStateOf<String?>(null)
    var errorEmail by mutableStateOf<String?>(null)
    var errorPassword by mutableStateOf<String?>(null)
    var errorConfirmar by mutableStateOf<String?>(null)
    var errorGeneral by mutableStateOf<String?>(null)

    var cargando by mutableStateOf(false)
    var registroExitoso by mutableStateOf(false)

    fun cambiarNombre(v: String) { nombre = v.take(60); errorNombre = null }
    fun cambiarEmail(v: String) { email = v.take(80); errorEmail = null; errorGeneral = null }
    fun cambiarPassword(v: String) { password = v.take(40); errorPassword = null }
    fun cambiarConfirmar(v: String) { confirmar = v.take(40); errorConfirmar = null }
    fun cambiarRol(v: String) { rol = v }

    /** Valida todos los campos y marca los errores. Devuelve true si el formulario es válido. */
    private fun validar(): Boolean {
        val n = nombre.trim()
        errorNombre = when {
            n.isEmpty() -> "Ingresa tu nombre"
            n.length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }
        val correo = email.trim()
        errorEmail = when {
            correo.isEmpty() -> "Ingresa tu correo"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "El correo no tiene un formato válido"
            else -> null
        }
        errorPassword = when {
            password.isEmpty() -> "Ingresa una contraseña"
            password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
        errorConfirmar = if (confirmar != password) "Las contraseñas no coinciden" else null
        return listOf(errorNombre, errorEmail, errorPassword, errorConfirmar).all { it == null }
    }

    fun registrar() {
        if (cargando || !validar()) return
        errorGeneral = null
        cargando = true

        val correo = email.trim().lowercase()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                SeedData.cargarSiEstaVacia(db) // si la BD está vacía, primero crea los datos de prueba

                if (db.usuarioDao().buscarPorEmail(correo) != null) {
                    errorEmail = "Este correo ya está registrado"
                    return@launch
                }
                // insertar() devuelve el id generado
                val id = db.usuarioDao().insertar(
                    Usuario(nombre = nombre.trim(), email = correo, password = password, rol = rol)
                ).toInt()
                // Queda con la sesión iniciada, igual que después de un login
                Sesion.guardar(
                    getApplication(),
                    Usuario(id = id, nombre = nombre.trim(), email = correo, password = "", rol = rol)
                )
                registroExitoso = true
            } catch (e: SQLiteConstraintException) {
                errorEmail = "Este correo ya está registrado"   // otro registro con el mismo correo justo antes
            } catch (e: Exception) {
                errorGeneral = "Ocurrió un error al crear la cuenta. Intenta nuevamente."
            } finally {
                cargando = false
            }
        }
    }
}
