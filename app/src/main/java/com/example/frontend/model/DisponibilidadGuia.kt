package com.example.frontend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Bloque de horario en que un guía puede atender visitas en un recinto
@Entity(tableName = "disponibilidad_guia")
data class DisponibilidadGuia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val guiaId: Int,
    val recintoId: Int,
    val dia: Int,                     // 1 = lunes ... 7 = domingo
    val inicio: String,               // "10:00"
    val fin: String                   // "14:00"
)
