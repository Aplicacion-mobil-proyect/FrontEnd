package com.example.frontend.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/*
 * Guarda el resultado del último QR escaneado.
 * Los QR de la app tienen el formato "recinto:<id>", por ejemplo "recinto:1" (Catedral).
 */
class QrViewModel : ViewModel() {

    private val _contenidoQR = MutableStateFlow("")
    val contenidoQR: StateFlow<String> = _contenidoQR.asStateFlow()

    // Id del recinto leído desde el QR, o null si el QR no es de la app
    private val _recintoId = MutableStateFlow<Int?>(null)
    val recintoId: StateFlow<Int?> = _recintoId.asStateFlow()

    fun guardarQR(contenido: String) {
        _contenidoQR.value = contenido
        _recintoId.value = obtenerRecintoId(contenido)
    }

    // "recinto:3" -> 3   |   cualquier otro texto -> null
    private fun obtenerRecintoId(contenido: String): Int? {
        val texto = contenido.trim().lowercase()
        if (!texto.startsWith("recinto:")) return null
        return texto.removePrefix("recinto:").toIntOrNull()
    }
}
