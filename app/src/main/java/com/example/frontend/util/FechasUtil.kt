package com.example.frontend.util

import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Valores que se guardan en la base de datos (se traducen al mostrarlos, ver view/components/Traducciones.kt)
val TIPOS_EVENTO = listOf("Música sacra", "Exposición", "Litúrgico")

object FechasUtil {
    private val regexHora = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

    // SimpleDateFormat no es thread-safe: se crea uno nuevo en cada uso.
    // Los formatos numéricos usan Locale.US para que no cambien con el idioma; los que llevan nombres usan el idioma elegido.
    private fun fmt(p: String) = SimpleDateFormat(p, Locale.US).apply { isLenient = false }
    private fun fmtIdioma(p: String) = SimpleDateFormat(p, Locale.getDefault()).apply { isLenient = false }

    fun hoyIso(): String = fmt("yyyy-MM-dd").format(Date())

    fun horaValida(h: String) = regexHora.matches(h)

    /** "2026-11-14" -> "Sábado 14 de noviembre de 2026" (o su equivalente en el idioma elegido) */
    fun isoALegible(iso: String): String = try {
        val d = fmt("yyyy-MM-dd").parse(iso)
        val patron = when (Locale.getDefault().language) {
            "en" -> "EEEE, MMMM d, yyyy"
            "fr" -> "EEEE d MMMM yyyy"
            "de" -> "EEEE, d. MMMM yyyy"
            "ru" -> "EEEE, d MMMM yyyy 'г.'"
            else -> "EEEE d 'de' MMMM 'de' yyyy"
        }
        if (d == null) iso else fmtIdioma(patron).format(d).replaceFirstChar { it.uppercase() }
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

    /** Día de la semana de una fecha ISO: 1 = lunes ... 7 = domingo (igual que Horario.dia). */
    fun diaSemanaIso(iso: String): Int {
        val d = fmt("yyyy-MM-dd").parse(iso) ?: return 1
        val dow = Calendar.getInstance().apply { time = d }.get(Calendar.DAY_OF_WEEK) // domingo = 1
        return (dow + 5) % 7 + 1
    }

    /** Fecha ISO de hoy + n días. */
    fun isoMasDias(dias: Int): String {
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dias) }
        return fmt("yyyy-MM-dd").format(c.time)
    }

    /** "09:30" -> 570 */
    fun minutos(hora: String): Int = hora.substring(0, 2).toInt() * 60 + hora.substring(3, 5).toInt()

    /** 570 -> "09:30" */
    fun horaDesdeMinutos(min: Int): String = String.format(Locale.US, "%02d:%02d", min / 60, min % 60)

    /** 1 = lunes ... 7 = domingo, con el nombre en el idioma elegido ("Lunes", "Monday", "Lundi"...). */
    fun nombreDia(dia: Int): String {
        val nombres = DateFormatSymbols(Locale.getDefault()).weekdays // índice 1 = domingo ... 7 = sábado
        val indice = (dia % 7) + 1
        return nombres.getOrNull(indice)?.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() } ?: "Día $dia"
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
