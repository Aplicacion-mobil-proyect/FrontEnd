package com.example.frontend.util

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.frontend.ui.theme.Paleta

/*
 * Guarda la paleta de colores elegida en SharedPreferences.
 * paletaActual es un estado de Compose: al cambiarlo, toda la app se redibuja
 * con los colores nuevos al instante (no hace falta reiniciar como con el idioma).
 */
object TemaManager {
    private const val ARCHIVO = "ajustes"
    private const val CLAVE = "paleta"

    var paletaActual by mutableStateOf(Paleta.BURDEO)
        private set

    /** Se llama una vez en MainActivity, antes de dibujar la app. */
    fun cargar(context: Context) {
        val guardada = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).getString(CLAVE, null)
        paletaActual = Paleta.values().firstOrNull { it.name == guardada } ?: Paleta.BURDEO
    }

    fun cambiar(context: Context, paleta: Paleta) {
        paletaActual = paleta
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).edit().putString(CLAVE, paleta.name).apply()
    }
}
