package com.example.frontend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recinto")
data class Recinto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,                 // Catedral, Basílica, Iglesia, Museo...
    val direccion: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    val descripcion: String,          // reseña histórica
    val estado: String = EstadosRecinto.ABIERTO,
    val duracionVisita: Int = 30      // minutos estimados de visita
)
