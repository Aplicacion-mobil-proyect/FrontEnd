package com.example.frontend.view.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.frontend.util.FechasUtil
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Campo de fecha que abre un calendario. Muestra la fecha como dd/MM/aaaa.
 * En el calendario solo se pueden elegir días desde hoy hasta [maxDiasAnticipacion] días más
 * y que sean de la semana en que el recinto atiende ([diasAtencion]: 1 = lunes ... 7 = domingo).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatCampoFecha(
    valor: String,
    error: String?,
    diasAtencion: Set<Int>,
    maxDiasAnticipacion: Int,
    onFechaElegida: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Fecha"
) {
    var mostrar by remember { mutableStateOf(false) }

    // Tocar el campo (no solo el ícono) abre el calendario
    val interaccion = remember { MutableInteractionSource() }
    LaunchedEffect(interaccion) {
        interaccion.interactions.collect { if (it is PressInteraction.Release) mostrar = true }
    }

    OutlinedTextField(
        value = valor,
        onValueChange = {},               // no se escribe a mano: se elige en el calendario
        readOnly = true,
        interactionSource = interaccion,
        label = { Text(etiqueta) },
        placeholder = { Text("Toca para elegir") },
        trailingIcon = {
            IconButton(onClick = { mostrar = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Elegir fecha")
            }
        },
        isError = error != null,
        supportingText = if (error != null) { { Text(error) } } else null,
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )

    if (mostrar) {
        val hoy = FechasUtil.hoyIso()
        val limite = FechasUtil.isoMasDias(maxDiasAnticipacion)
        val permitidas = remember(diasAtencion, hoy) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val iso = isoDesdeUtc(utcTimeMillis)
                    return iso >= hoy && iso <= limite && diaIsoDesdeUtc(utcTimeMillis) in diasAtencion
                }
                override fun isSelectableYear(year: Int) = true
            }
        }
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = millisUtcDesde(valor),
            selectableDates = permitidas
        )
        DatePickerDialog(
            onDismissRequest = { mostrar = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        estado.selectedDateMillis?.let { onFechaElegida(ddMmYyyyDesdeUtc(it)) }
                        mostrar = false
                    },
                    enabled = estado.selectedDateMillis != null
                ) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrar = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = estado)
        }
    }
}

// El calendario de Material entrega la fecha como medianoche en UTC: se lee en UTC para no correr un día.
private fun calendarioUtc(ms: Long): Calendar =
    Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = ms }

private fun isoDesdeUtc(ms: Long): String {
    val c = calendarioUtc(ms)
    return String.format(Locale.US, "%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
}

/** 1 = lunes ... 7 = domingo (igual que Horario.dia). */
private fun diaIsoDesdeUtc(ms: Long): Int = (calendarioUtc(ms).get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1

private fun ddMmYyyyDesdeUtc(ms: Long): String {
    val c = calendarioUtc(ms)
    return String.format(Locale.US, "%02d/%02d/%04d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))
}

/** "25/12/2026" -> millis UTC de ese día (o null si el texto aún no es una fecha válida). */
private fun millisUtcDesde(texto: String): Long? {
    val iso = FechasUtil.ddMmYyyyAIso(texto) ?: return null
    val (y, m, d) = iso.split("-").map { it.toInt() }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { clear(); set(y, m - 1, d) }.timeInMillis
}
