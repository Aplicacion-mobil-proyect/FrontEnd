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
import com.example.frontend.model.Usuario
import com.example.frontend.repository.UsuarioRepository
import com.example.frontend.session.Sesion
import kotlinx.coroutines.launch

/*
 * Editar perfil: nombre, apodo, correo, color del avatar y (opcional) contraseña.
 * Los errores se guardan como id de texto (R.string...) para que la pantalla
 * los muestre en el idioma elegido.
 */
class EditarPerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())

    // Usuario tal como está en la base de datos (se carga en init)
    private var original: Usuario? = null

    // Campos del formulario
    var nombre by mutableStateOf("")
    var apodo by mutableStateOf("")
    var email by mutableStateOf("")
    var avatar by mutableStateOf(0)
    var passwordActual by mutableStateOf("")
    var passwordNueva by mutableStateOf("")
    var passwordConfirmar by mutableStateOf("")

    // Errores por campo: id del texto (R.string...) o null si no hay error
    var errorNombre by mutableStateOf<Int?>(null)
    var errorApodo by mutableStateOf<Int?>(null)
    var errorEmail by mutableStateOf<Int?>(null)
    var errorPasswordActual by mutableStateOf<Int?>(null)
    var errorPasswordNueva by mutableStateOf<Int?>(null)
    var errorPasswordConfirmar by mutableStateOf<Int?>(null)
    var errorGeneral by mutableStateOf<Int?>(null)

    var cargando by mutableStateOf(true)
    var guardadoExitoso by mutableStateOf(false)

    init {
        viewModelScope.launch {
            val sesion = Sesion.obtener(getApplication())
            val usuario = sesion?.let { repository.buscarPorId(it.id) }
            if (usuario != null) {
                original = usuario
                nombre = usuario.nombre
                apodo = usuario.apodo ?: ""
                email = usuario.email
                avatar = usuario.avatar
            } else {
                errorGeneral = R.string.error_sin_usuario
            }
            cargando = false
        }
    }

    fun cambiarNombre(v: String) { nombre = v.take(60); errorNombre = null }
    fun cambiarApodo(v: String) { apodo = v.take(20); errorApodo = null }
    fun cambiarEmail(v: String) { email = v.take(80); errorEmail = null }
    fun cambiarAvatar(v: Int) { avatar = v }
    fun cambiarPasswordActual(v: String) { passwordActual = v.take(40); errorPasswordActual = null }
    fun cambiarPasswordNueva(v: String) { passwordNueva = v.take(40); errorPasswordNueva = null }
    fun cambiarPasswordConfirmar(v: String) { passwordConfirmar = v.take(40); errorPasswordConfirmar = null }

    /** La contraseña solo se cambia si el usuario escribió algo en alguno de los 3 campos. */
    private fun quiereCambiarPassword() =
        passwordActual.isNotEmpty() || passwordNueva.isNotEmpty() || passwordConfirmar.isNotEmpty()

    private fun validar(usuario: Usuario): Boolean {
        val n = nombre.trim()
        errorNombre = when {
            n.isEmpty() -> R.string.error_nombre_vacio
            n.length < 3 -> R.string.error_minimo_3
            else -> null
        }
        val a = apodo.trim()
        errorApodo = if (a.isNotEmpty() && a.length < 2) R.string.error_apodo_corto else null

        val correo = email.trim()
        errorEmail = when {
            correo.isEmpty() -> R.string.error_correo_vacio
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> R.string.error_correo_formato
            else -> null
        }

        if (quiereCambiarPassword()) {
            errorPasswordActual = if (passwordActual != usuario.password) R.string.error_password_actual else null
            errorPasswordNueva = when {
                passwordNueva.length < 6 -> R.string.error_minimo_6
                passwordNueva == passwordActual -> R.string.error_password_igual
                else -> null
            }
            errorPasswordConfirmar = if (passwordConfirmar != passwordNueva) R.string.error_password_no_coincide else null
        }
        return listOf(errorNombre, errorApodo, errorEmail, errorPasswordActual, errorPasswordNueva, errorPasswordConfirmar)
            .all { it == null }
    }

    fun guardar() {
        val usuario = original ?: return
        if (cargando || !validar(usuario)) return
        cargando = true
        errorGeneral = null

        viewModelScope.launch {
            val correo = email.trim().lowercase()
            // Si cambió el correo, revisa que no lo use otra cuenta
            if (correo != usuario.email && repository.existeEmail(correo)) {
                errorEmail = R.string.error_correo_registrado
                cargando = false
                return@launch
            }
            val actualizado = usuario.copy(
                nombre = nombre.trim(),
                apodo = apodo.trim().ifEmpty { null },
                email = correo,
                avatar = avatar,
                password = if (quiereCambiarPassword()) passwordNueva else usuario.password
            )
            try {
                repository.actualizar(actualizado)                                // UPDATE en Room
                Sesion.guardar(getApplication(), actualizado.copy(password = "")) // la sesión no guarda la clave
                guardadoExitoso = true
            } catch (e: Exception) {
                errorGeneral = R.string.error_guardar
            } finally {
                cargando = false
            }
        }
    }
}
