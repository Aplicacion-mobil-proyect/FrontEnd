package com.example.frontend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ruta")
data class Ruta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,
    val duracion: Int                 // minutos estimados
)
