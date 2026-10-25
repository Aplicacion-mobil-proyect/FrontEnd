package com.example.frontend.model

// No es una tabla: es el resultado de un JOIN entre visita, recinto y guía
data class VisitaDetalle(
    val id: Int,
    val recintoNombre: String,
    val fecha: String,
    val hora: String,
    val personas: Int,
    val conGuia: Boolean,
    val guiaNombre: String?,
    val estado: String
)
