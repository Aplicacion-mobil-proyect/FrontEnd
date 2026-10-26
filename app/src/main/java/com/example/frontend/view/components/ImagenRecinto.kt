package com.example.frontend.view.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.frontend.R
import com.example.frontend.model.Recinto

/*
 * Imagen del recinto (recurso multimedia de la ficha).
 *
 * 1. Si existe una foto en res/drawable llamada "recinto_<id>" (ej. recinto_1.jpg), muestra esa foto.
 * 2. Si no, muestra una ilustración según el tipo de recinto (catedral, iglesia, basílica o museo).
 *
 * Para agregar fotos reales: copien la imagen a app/src/main/res/drawable con el nombre recinto_<id>.jpg
 * (en minúsculas, sin espacios). No hay que cambiar código.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun ImagenRecinto(recinto: Recinto, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imagen = remember(recinto.id, recinto.tipo) {
        val fotoId = context.resources.getIdentifier("recinto_${recinto.id}", "drawable", context.packageName)
        if (fotoId != 0) fotoId else ilustracionPorTipo(recinto.tipo)
    }

    Image(
        painter = painterResource(id = imagen),
        contentDescription = "Imagen de ${recinto.nombre}",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

// Elige la ilustración según el tipo de recinto
fun ilustracionPorTipo(tipo: String): Int = when {
    tipo.contains("Catedral", ignoreCase = true) -> R.drawable.ilustracion_catedral
    tipo.contains("Basílica", ignoreCase = true) || tipo.contains("Santuario", ignoreCase = true) -> R.drawable.ilustracion_basilica
    tipo.contains("Museo", ignoreCase = true) || tipo.contains("Archivo", ignoreCase = true) -> R.drawable.ilustracion_museo
    else -> R.drawable.ilustracion_iglesia
}
