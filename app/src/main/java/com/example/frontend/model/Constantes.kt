package com.example.frontend.model

// Valores fijos que se guardan como texto en la base de datos

object Roles {
    const val TURISTA = "TURISTA"
    const val COLEGIO = "COLEGIO"
    const val ENCARGADO = "ENCARGADO"
}

object EstadosRecinto {
    const val ABIERTO = "ABIERTO"
    const val CERRADO = "CERRADO"
    const val EN_CELEBRACION = "EN_CELEBRACION"
    val todos = listOf(ABIERTO, CERRADO, EN_CELEBRACION)
}

object EstadosVisita {
    const val AGENDADA = "AGENDADA"
    const val CANCELADA = "CANCELADA"
}
