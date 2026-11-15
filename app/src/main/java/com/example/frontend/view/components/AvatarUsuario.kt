package com.example.frontend.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.theme.ColoresAvatar

/** Iniciales del nombre: "Turista Demo" -> "TD", "Ana" -> "A". */
fun iniciales(nombre: String): String =
    nombre.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

/** Círculo de color con las iniciales del usuario. El color es el que eligió en "Editar perfil". */
@Composable
fun AvatarUsuario(nombre: String, avatar: Int, modifier: Modifier = Modifier, tamano: Dp = 88.dp) {
    val color = ColoresAvatar[avatar.coerceIn(0, ColoresAvatar.lastIndex)]
    Box(
        modifier = modifier.size(tamano).background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            iniciales(nombre),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = (tamano.value * 0.38f).sp   // el texto crece con el círculo
        )
    }
}
