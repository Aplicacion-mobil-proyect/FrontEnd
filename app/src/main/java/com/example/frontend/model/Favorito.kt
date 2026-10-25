package com.example.frontend.model

import androidx.room.Entity

// Un usuario puede marcar muchos recintos como favoritos (llave compuesta)
@Entity(tableName = "favorito", primaryKeys = ["usuarioId", "recintoId"])
data class Favorito(
    val usuarioId: Int,
    val recintoId: Int
)
