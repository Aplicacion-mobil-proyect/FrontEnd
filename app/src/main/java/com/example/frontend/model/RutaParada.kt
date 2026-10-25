package com.example.frontend.model

import androidx.room.Entity

// Cada parada de una ruta: qué recinto y en qué orden
@Entity(tableName = "ruta_parada", primaryKeys = ["rutaId", "orden"])
data class RutaParada(
    val rutaId: Int,
    val orden: Int,
    val recintoId: Int
)
