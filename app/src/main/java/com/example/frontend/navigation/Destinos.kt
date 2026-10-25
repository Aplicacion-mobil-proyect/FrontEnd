package com.example.frontend.navigation

/**
 * Arma las rutas con argumentos a partir de las constantes de Rutas, reemplazando el {parámetro}.
 * Así no dependemos de cómo estén escritas las rutas dentro de Rutas.kt.
 */
object Destinos {
    fun eventoDetalle(eventoId: Int) = Rutas.EVENTO_DETALLE.replace("{eventoId}", eventoId.toString())
    fun ficha(recintoId: Int) = Rutas.FICHA.replace("{recintoId}", recintoId.toString())
    /** eventoId = 0 -> crear evento nuevo; > 0 -> editar ese evento. */
    fun eventoForm(eventoId: Int) = Rutas.ENC_EVENTO_FORM.replace("{eventoId}", eventoId.toString())
}
