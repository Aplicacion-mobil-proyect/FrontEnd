package com.example.frontend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visita")
data class Visita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val recintoId: Int,
    val fecha: String,                // "2026-11-05"
    val hora: String,                 // "10:30"
    val personas: Int,
    val conGuia: Boolean,
    val guiaId: Int? = null,
    val estado: String = EstadosVisita.AGENDADA
)
