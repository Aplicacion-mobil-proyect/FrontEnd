package com.example.frontend.model

// No es una tabla: es el resultado de un JOIN entre evento y recinto
data class EventoConRecinto(
    val id: Int,
    val recintoId: Int,
    val recintoNombre: String,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val fecha: String,
    val hora: String
)
