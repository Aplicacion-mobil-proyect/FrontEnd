package com.example.frontend.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.frontend.ui.theme.colorEstado
import com.example.frontend.ui.theme.textoEstado

// Etiqueta de color con el estado del recinto (Abierto / Cerrado / En celebración)
@Composable
fun ChipEstado(estado: String, modifier: Modifier = Modifier) {
    Text(
        text = textoEstado(estado),
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(colorEstado(estado), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
