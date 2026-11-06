package com.example.frontend.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

enum class Idioma(val codigo: String, val nombre: String) {
    ES("es", "Español"),
    EN("en", "English"),
    FR("fr", "Français"),
    DE("de", "Deutsch"),
    RU("ru", "Русский")
}

/** Guarda el idioma elegido y entrega un Context que usa ese idioma. */
object IdiomaManager {
    private const val ARCHIVO = "ajustes"
    private const val CLAVE = "idioma"

    /** Idioma elegido; si nunca se eligió, el del teléfono (si es uno de los 5) o español. */
    fun obtener(context: Context): Idioma {
        val guardado = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).getString(CLAVE, null)
        return Idioma.values().firstOrNull { it.codigo == guardado }
            ?: Idioma.values().firstOrNull { it.codigo == Locale.getDefault().language }
            ?: Idioma.ES
    }

    fun guardar(context: Context, idioma: Idioma) {
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).edit().putString(CLAVE, idioma.codigo).apply()
    }

    /**
     * Context con el idioma elegido. Se usa en MainActivity.attachBaseContext
     * y también en los ViewModels cuando necesiten un texto traducido.
     */
    fun envolver(context: Context): Context {
        val locale = Locale(obtener(context).codigo)
        Locale.setDefault(locale) // también cambia los nombres de días y meses de las fechas
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
