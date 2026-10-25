package com.example.frontend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evento")
data class Evento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recintoId: Int,
    val titulo: String,
    val descripcion: String,
    val tipo: String,                 // Música sacra, Exposición, Litúrgico...
    val fecha: String,                // "2026-11-14"
    val hora: String                  // "19:00"
)
