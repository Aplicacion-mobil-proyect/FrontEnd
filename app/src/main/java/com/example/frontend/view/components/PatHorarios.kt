package com.example.frontend.view.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.frontend.model.Horario
import com.example.frontend.util.FechasUtil

/** Horario semanal (lunes a domingo) y, si hay, los horarios especiales. */
@Composable
fun PatHorarios(horarios: List<Horario>) {
    val regulares = horarios.filter { !it.especial }.associateBy { it.dia }
    (1..7).forEach { dia ->
        val h = regulares[dia]
        Text(
            "${FechasUtil.nombreDia(dia)}: " + (if (h != null) "${h.apertura} – ${h.cierre}" else "Cerrado"),
            style = MaterialTheme.typography.bodyMedium
        )
    }
    horarios.filter { it.especial }.forEach {
        Text(
            "${FechasUtil.nombreDia(it.dia)} · ${it.descripcion.ifBlank { "Horario especial" }}: ${it.apertura} – ${it.cierre}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
