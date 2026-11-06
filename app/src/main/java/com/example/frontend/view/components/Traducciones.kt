package com.example.frontend.view.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.frontend.R

// En la base de datos se guardan valores en español (ej. "Música sacra", "ABIERTO").
// Estas funciones los muestran en el idioma elegido; si llega un valor desconocido, lo muestran tal cual.

@Composable
fun etiquetaEstadoRecinto(estado: String): String = when (estado) {
    "ABIERTO" -> stringResource(R.string.abierto)
    "CERRADO" -> stringResource(R.string.cerrado)
    "EN_CELEBRACION" -> stringResource(R.string.en_celebracion)
    else -> estado
}

@Composable
fun tipoEventoTexto(tipo: String): String = when (tipo) {
    "Música sacra" -> stringResource(R.string.tipo_musica_sacra)
    "Exposición" -> stringResource(R.string.tipo_exposicion)
    "Litúrgico" -> stringResource(R.string.tipo_liturgico)
    else -> tipo
}

@Composable
fun tipoRecintoTexto(tipo: String): String = when (tipo) {
    "Catedral" -> stringResource(R.string.tipo_catedral)
    "Basílica" -> stringResource(R.string.tipo_basilica)
    "Iglesia" -> stringResource(R.string.tipo_iglesia)
    "Museo" -> stringResource(R.string.tipo_museo)
    "Santuario" -> stringResource(R.string.tipo_santuario)
    "Templo" -> stringResource(R.string.tipo_templo)
    else -> tipo
}
