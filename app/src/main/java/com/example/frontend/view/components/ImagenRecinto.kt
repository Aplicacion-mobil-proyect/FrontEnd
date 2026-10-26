package com.example.frontend.view.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.frontend.R

/**
 * Foto de cada recinto, según su id en SeedData.
 * Si se agrega un recinto nuevo: guarda su foto en res/drawable-nodpi (nombre en minúsculas, sin tildes)
 * y agrega una línea aquí. Los recintos sin foto (ej. Los Dominicos) muestran un ícono.
 */
@DrawableRes
fun imagenDeRecinto(recintoId: Int): Int? = when (recintoId) {
    1 -> R.drawable.img_catedral
    2 -> R.drawable.img_merced
    3 -> R.drawable.img_san_francisco
    4 -> R.drawable.img_santo_domingo
    5 -> R.drawable.img_sacramentinos
    6 -> R.drawable.img_museo_recoleta
    7 -> R.drawable.img_santuario_inmaculada
    8 -> R.drawable.img_lourdes
    9 -> R.drawable.img_templo_votivo
    10 -> R.drawable.img_museo_carmen
    12 -> R.drawable.img_divina_providencia
    else -> null
}

/**
 * Muestra la foto del recinto recortada para llenar el espacio que le des con el modifier.
 * Ejemplos:  ImagenRecinto(r.id, r.nombre, Modifier.size(72.dp))
 *            ImagenRecinto(r.id, r.nombre, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
 */
@Composable
fun ImagenRecinto(
    recintoId: Int,
    nombre: String,
    modifier: Modifier = Modifier,
    forma: Shape = RoundedCornerShape(12.dp)
) {
    val recurso = imagenDeRecinto(recintoId)
    if (recurso != null) {
        Image(
            painter = painterResource(recurso),
            contentDescription = "Fachada de $nombre",
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(forma)
        )
    } else {
        // Sin foto: recuadro con un ícono
        Box(
            modifier.clip(forma).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
