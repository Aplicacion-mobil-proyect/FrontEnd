package com.example.frontend.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuario", indices = [Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String,              // Roles.TURISTA, Roles.COLEGIO o Roles.ENCARGADO
    val recintoId: Int? = null,   // solo el ENCARGADO tiene un recinto asignado
    val apodo: String? = null,    // opcional; si existe, se usa para saludar en la Home
    val avatar: Int = 0           // posición del color elegido en ColoresAvatar (0 a 5)
)
