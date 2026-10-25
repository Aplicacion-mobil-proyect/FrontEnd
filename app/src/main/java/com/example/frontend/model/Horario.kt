package com.example.frontend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horario")
data class Horario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recintoId: Int,
    val dia: Int,                     // 1 = lunes ... 7 = domingo
    val apertura: String,             // "09:00"
    val cierre: String,               // "19:00"
    val especial: Boolean = false,    // horario especial (Semana Santa, fiesta, etc.)
    val descripcion: String = ""
)
