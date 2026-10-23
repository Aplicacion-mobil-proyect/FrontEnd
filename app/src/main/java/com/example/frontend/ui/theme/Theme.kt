package com.example.frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.frontend.model.EstadosRecinto

private val LightColorScheme = lightColorScheme(
    primary = Burdeo,
    onPrimary = Color.White,
    primaryContainer = BurdeoContenedor,
    onPrimaryContainer = BurdeoOscuroContenedor,
    secondary = Dorado,
    onSecondary = Color.White,
    secondaryContainer = DoradoContenedor,
    onSecondaryContainer = DoradoOscuroContenedor,
    tertiary = Piedra,
    background = FondoClaro,
    surface = FondoClaro
)

private val DarkColorScheme = darkColorScheme(
    primary = BurdeoClaro,
    onPrimary = BurdeoOscuroContenedor,
    primaryContainer = BurdeoOscuroContenedor,
    onPrimaryContainer = BurdeoContenedor,
    secondary = DoradoClaro,
    onSecondary = DoradoOscuroContenedor,
    secondaryContainer = DoradoOscuroContenedor,
    onSecondaryContainer = DoradoContenedor,
    tertiary = PiedraClaro,
    background = FondoOscuro,
    surface = FondoOscuro
)

/**
 * Tema de la app. No usa colores dinámicos (los del fondo de pantalla del celular)
 * para mantener la identidad visual del Arzobispado en todos los dispositivos.
 */
@Composable
fun FrontEndTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}

/** Color asociado a cada estado de recinto. Úsenlo en mapa, ficha y pantallas de encargado. */
fun colorEstado(estado: String): Color = when (estado) {
    EstadosRecinto.ABIERTO -> EstadoAbierto
    EstadosRecinto.CERRADO -> EstadoCerrado
    EstadosRecinto.EN_CELEBRACION -> EstadoCelebracion
    else -> Piedra
}

/** Texto legible de cada estado. */
fun textoEstado(estado: String): String = when (estado) {
    EstadosRecinto.ABIERTO -> "Abierto"
    EstadosRecinto.CERRADO -> "Cerrado"
    EstadosRecinto.EN_CELEBRACION -> "En celebración"
    else -> estado
}
