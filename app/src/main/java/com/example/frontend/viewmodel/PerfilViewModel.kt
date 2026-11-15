package com.example.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.model.Usuario
import com.example.frontend.session.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Totales que se muestran en la tarjeta "Mi actividad". */
data class Actividad(val favoritos: Int = 0, val visitas: Int = 0, val rutas: Int = 0)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).usuarioDao()
    private val sesion: Usuario? = Sesion.obtener(application)

    /**
     * Usuario leído desde Room con Flow: al volver de "Editar perfil" la pantalla
     * ya muestra los datos nuevos sin recargar. Si no está en la BD, usa el de la sesión.
     */
    val usuario: StateFlow<Usuario?> =
        (if (sesion == null) flowOf(null) else dao.observarPorId(sesion.id).map { it ?: sesion })
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), sesion)

    /** Junta los 3 contadores (favoritos, visitas y rutas) en un solo objeto. */
    val actividad: StateFlow<Actividad> =
        (if (sesion == null) flowOf(Actividad())
        else combine(
            dao.contarFavoritos(sesion.id),
            dao.contarVisitas(sesion.id),
            dao.contarRutas(sesion.id)
        ) { favoritos, visitas, rutas -> Actividad(favoritos, visitas, rutas) })
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Actividad())

    fun cerrarSesion() = Sesion.cerrar(getApplication())
}
