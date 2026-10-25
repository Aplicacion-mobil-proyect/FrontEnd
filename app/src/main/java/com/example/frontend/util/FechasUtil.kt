package com.example.frontend.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val TIPOS_EVENTO = listOf("Música sacra", "Exposición", "Litúrgico")

fun etiquetaEstadoRecinto(estado: String) = when (estado) {
    "ABIERTO" -> "Abierto"
    "CERRADO" -> "Cerrado"
    "EN_CELEBRACION" -> "En celebración"
    else -> estado
}

object FechasUtil {
    private val cl = Locale("es", "CL")
    private val regexHora = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

    // SimpleDateFormat no es thread-safe: se crea uno nuevo en cada uso
    private fun fmt(p: String) = SimpleDateFormat(p, cl).apply { isLenient = false }

    fun hoyIso(): String = fmt("yyyy-MM-dd").format(Date())

    fun horaValida(h: String) = regexHora.matches(h)

    /** "2026-11-14" -> "Sábado 14 de noviembre de 2026" */
    fun isoALegible(iso: String): String = try {
        val d = fmt("yyyy-MM-dd").parse(iso)
        if (d == null) iso else fmt("EEEE d 'de' MMMM 'de' yyyy").format(d).replaceFirstChar { it.uppercase() }
    } catch (e: ParseException) { iso }

    /** "2026-11-14" -> "14/11/2026" */
    fun isoADdMmYyyy(iso: String): String = try {
        val d = fmt("yyyy-MM-dd").parse(iso)
        if (d == null) iso else fmt("dd/MM/yyyy").format(d)
    } catch (e: ParseException) { iso }

    /** "14/11/2026" -> "2026-11-14". Devuelve null si el texto no es una fecha real (ej. 31/02/2026). */
    fun ddMmYyyyAIso(texto: String): String? = try {
        val d = fmt("dd/MM/yyyy").parse(texto.trim())
        if (d == null) null else fmt("yyyy-MM-dd").format(d)
    } catch (e: ParseException) { null }

    /** true si la fecha ISO + hora ya pasaron. */
    fun yaPaso(iso: String, hora: String): Boolean = try {
        val d = fmt("yyyy-MM-dd HH:mm").parse("$iso $hora")
        d != null && d.before(Date())
    } catch (e: ParseException) { false }

    fun nombreDia(dia: Int) = when (dia) {
        1 -> "Lunes"; 2 -> "Martes"; 3 -> "Miércoles"; 4 -> "Jueves"
        5 -> "Viernes"; 6 -> "Sábado"; 7 -> "Domingo"; else -> "Día $dia"
    }
}

object Validar {
    /** Devuelve el mensaje de error o null si el texto es válido. */
    fun texto(v: String, etiqueta: String, min: Int, max: Int): String? {
        val t = v.trim()
        return when {
            t.isEmpty() -> "Ingresa $etiqueta"
            t.length < min -> "Mínimo $min caracteres"
            t.length > max -> "Máximo $max caracteres"
            else -> null
        }
    }
}
