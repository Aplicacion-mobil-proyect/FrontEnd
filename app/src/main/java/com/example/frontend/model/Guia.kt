package com.example.frontend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guia")
data class Guia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val idiomas: String
)
