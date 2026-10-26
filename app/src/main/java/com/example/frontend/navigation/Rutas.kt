package com.example.frontend.navigation

// Nombres de todas las pantallas. Las que reciben un parámetro tienen una función para armar la ruta.
// Ejemplo:  navController.navigate(Rutas.ficha(3))
object Rutas {
    // Acceso
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    // Turista / colegio
    const val HOME = "home"
    const val MAPA = "mapa"
    const val CERCANOS = "cercanos"
    const val ESCANER_QR = "escaner_qr"
    const val FICHA = "ficha/{recintoId}"
    const val EVENTOS = "eventos"
    const val EVENTO_DETALLE = "evento/{eventoId}"
    const val FAVORITOS = "favoritos"
    const val MIS_VISITAS = "mis_visitas"
    const val PLANIFICAR_VISITA = "planificar/{recintoId}"
    const val MIS_RUTAS = "rutas"
    const val CREAR_RUTA = "crear_ruta"
    const val INICIAR_RUTA = "ruta/{rutaId}"
    const val PERFIL = "perfil"

    // Encargado
    const val ENC_MI_RECINTO = "enc_recinto"
    const val ENC_ESTADO = "enc_estado"
    const val ENC_HORARIOS = "enc_horarios"
    const val ENC_EVENTOS = "enc_eventos"
    const val ENC_EVENTO_FORM = "enc_evento/{eventoId}"     // eventoId = 0 significa "crear nuevo"
    const val ENC_GUIAS = "enc_guias"
    const val ENC_HORARIOS_ESPECIALES = "enc_horarios_especiales"

    fun ficha(recintoId: Int) = "ficha/$recintoId"
    fun eventoDetalle(eventoId: Int) = "evento/$eventoId"
    fun planificarVisita(recintoId: Int) = "planificar/$recintoId"
    fun iniciarRuta(rutaId: Int) = "ruta/$rutaId"
    fun encEventoForm(eventoId: Int) = "enc_evento/$eventoId"
}
