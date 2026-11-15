package com.example.frontend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.util.TemaManager

/** Arma el esquema claro de Material 3 con los colores de la paleta elegida. */
private fun esquemaClaro(p: Paleta) = lightColorScheme(
    primary = p.primario,
    onPrimary = Color.White,
    primaryContainer = p.primarioContenedor,
    onPrimaryContainer = p.enPrimarioContenedor,
    secondary = p.secundario,
    onSecondary = Color.White,
    secondaryContainer = p.secundarioContenedor,
    onSecondaryContainer = p.enSecundarioContenedor,
    tertiary = Piedra,
    background = p.fondo,
    onBackground = TextoPrincipal,
    surface = p.fondo,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario,   // textos secundarios y etiquetas de los campos
    outline = Borde                       // borde de los OutlinedTextField
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
 * Tema de la app. Siempre usa la paleta clara (aunque el celular esté en modo oscuro)
 * y no usa colores dinámicos, para mantener la identidad visual del Arzobispado.
 * Si algún día quieren modo oscuro, cambien darkTheme a isSystemInDarkTheme().
 */
@Composable
fun FrontEndTheme(
    darkTheme: Boolean = false,
    paleta: Paleta = TemaManager.paletaActual,   // la que eligió el usuario en el Perfil
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else esquemaClaro(paleta),
        typography = Typography
    ) {
        // Surface pinta el fondo y le da a los textos el color "onSurface" por defecto
        Surface(color = MaterialTheme.colorScheme.background, content = content)
    }
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
