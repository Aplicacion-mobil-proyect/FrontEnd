package com.example.frontend.ui.theme

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.frontend.R

/*
 * Paletas de colores que el usuario puede elegir en el Perfil.
 * Cada una define los colores principales del tema; los textos y bordes son los mismos para todas.
 */
enum class Paleta(
    @StringRes val nombre: Int,
    val primario: Color,
    val primarioContenedor: Color,
    val enPrimarioContenedor: Color,
    val secundario: Color,
    val secundarioContenedor: Color,
    val enSecundarioContenedor: Color,
    val fondo: Color,           // fondo general de las pantallas
    val fondoHome: Color,       // fondo de la Home
    val destacado: Color        // tarjeta grande "Explora el patrimonio"
) {
    BURDEO(
        R.string.paleta_burdeo,
        Burdeo, BurdeoContenedor, BurdeoOscuroContenedor,
        Dorado, DoradoContenedor, DoradoOscuroContenedor,
        FondoClaro, Color(0xFFF3EFEA), Color(0xFFDCD6D0)
    ),
    AZUL(
        R.string.paleta_azul,
        Color(0xFF1E4E79), Color(0xFFD3E4F5), Color(0xFF0B2A47),
        Dorado, DoradoContenedor, DoradoOscuroContenedor,
        Color(0xFFF7F9FC), Color(0xFFEDF1F5), Color(0xFFD5DEE7)
    ),
    VERDE(
        R.string.paleta_verde,
        Color(0xFF2E5E3A), Color(0xFFD4EBD6), Color(0xFF0F3319),
        Color(0xFF8A5A1F), Color(0xFFFBE0BE), Color(0xFF5A3A0A),
        Color(0xFFF7FAF5), Color(0xFFEEF2EA), Color(0xFFD6DFD0)
    ),
    MORADO(
        R.string.paleta_morado,
        Color(0xFF5B2E7A), Color(0xFFEBDCF6), Color(0xFF2E0F45),
        Dorado, DoradoContenedor, DoradoOscuroContenedor,
        Color(0xFFFAF7FC), Color(0xFFF2EEF5), Color(0xFFDDD5E3)
    ),
    GRAFITO(
        R.string.paleta_grafito,
        Color(0xFF37474F), Color(0xFFCFD8DC), Color(0xFF102027),
        Dorado, DoradoContenedor, DoradoOscuroContenedor,
        Color(0xFFF8F8F7), Color(0xFFEFEFEC), Color(0xFFD9D9D4)
    )
}
