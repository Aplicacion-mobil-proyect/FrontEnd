package com.example.frontend.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontend.R
import com.example.frontend.util.FechasUtil
import java.util.Calendar
import java.util.Locale

private const val PASO_MINUTOS = 15   // las horas se ofrecen cada 15 minutos

/**
 * Campo de hora con una rueda (horas y minutos) en vez de escribirla.
 * Solo ofrece horas en que se puede llegar: desde la apertura hasta (cierre - duración de la visita)
 * del día elegido, y si el día es hoy, solo las que aún no pasaron.
 */
@Composable
fun PatCampoHora(
    valor: String,
    error: String?,
    fechaIso: String?,        // fecha ya elegida (yyyy-MM-dd) o null
    apertura: String?,        // horario del recinto ese día, o null si no atiende
    cierre: String?,
    duracionMin: Int,
    onHoraElegida: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrar by remember { mutableStateOf(false) }
    val horas = remember(fechaIso, apertura, cierre, duracionMin) { horasDisponibles(fechaIso, apertura, cierre, duracionMin) }
    val puedeAbrir = fechaIso != null && horas.isNotEmpty()


    val ayuda = when {
        error != null -> error
        fechaIso == null -> stringResource(R.string.primero_elige_fecha)
        horas.isEmpty() -> stringResource(R.string.sin_horarios_disponibles)
        else -> null
    }

    // La capa transparente de encima recibe el toque: así abre aunque el campo sea de solo lectura
    Box(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},               // no se escribe a mano
            readOnly = true,
            label = { Text(stringResource(R.string.hora_llegada)) },
            placeholder = { Text(stringResource(R.string.toca_para_elegir)) },
            trailingIcon = {
                IconButton(onClick = { if (puedeAbrir) mostrar = true }) {
                    Icon(Icons.Filled.AccessTime, contentDescription = stringResource(R.string.elegir_hora))
                }
            },
            isError = error != null || (fechaIso != null && horas.isEmpty()),
            supportingText = if (ayuda != null) { { Text(ayuda) } } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            Modifier.matchParentSize().clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { if (puedeAbrir) mostrar = true }
        )
    }

    if (mostrar && puedeAbrir) {
        DialogoHora(
            horas = horas,
            valorActual = valor,
            onElegida = { onHoraElegida(it); mostrar = false },
            onCerrar = { mostrar = false }
        )
    }
}

/** Minutos desde medianoche en que se puede llegar. */
private fun horasDisponibles(fechaIso: String?, apertura: String?, cierre: String?, duracionMin: Int): List<Int> {
    if (fechaIso == null || apertura == null || cierre == null) return emptyList()
    val inicio = FechasUtil.minutos(apertura)
    val ultima = FechasUtil.minutos(cierre) - duracionMin
    if (ultima < inicio) return emptyList()
    val ahora = Calendar.getInstance()
    val minutosAhora = if (fechaIso == FechasUtil.hoyIso()) ahora.get(Calendar.HOUR_OF_DAY) * 60 + ahora.get(Calendar.MINUTE) else -1
    return generateSequence(inicio) { it + PASO_MINUTOS }.takeWhile { it <= ultima }.filter { it > minutosAhora }.toList()
}

private fun dosDigitos(n: Int) = String.format(Locale.US, "%02d", n)

@Composable
private fun DialogoHora(horas: List<Int>, valorActual: String, onElegida: (String) -> Unit, onCerrar: () -> Unit) {
    val inicial = if (FechasUtil.horaValida(valorActual)) FechasUtil.minutos(valorActual).takeIf { it in horas } else null
    var seleccion by remember { mutableStateOf(inicial ?: horas.first()) }   // minutos desde medianoche
    val listaHoras = remember(horas) { horas.map { it / 60 }.distinct() }
    fun minutosDe(h: Int) = horas.filter { it / 60 == h }.map { it % 60 }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(stringResource(R.string.hora_llegada)) },
        text = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                // Rueda de horas
                Rueda(
                    items = listaHoras.map { dosDigitos(it) },
                    indiceInicial = listaHoras.indexOf(seleccion / 60).coerceAtLeast(0),
                    onCambio = { i ->
                        val h = listaHoras[i]
                        val mins = minutosDe(h)
                        val m = if (seleccion % 60 in mins) seleccion % 60 else mins.first()
                        seleccion = h * 60 + m
                    }
                )
                Text(":", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 8.dp))
                // Rueda de minutos: se vuelve a crear cuando cambia la hora, porque cambian los minutos posibles
                key(seleccion / 60) {
                    val mins = minutosDe(seleccion / 60)
                    Rueda(
                        items = mins.map { dosDigitos(it) },
                        indiceInicial = mins.indexOf(seleccion % 60).coerceAtLeast(0),
                        onCambio = { i -> seleccion = (seleccion / 60) * 60 + mins[i] }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onElegida(FechasUtil.horaDesdeMinutos(seleccion)) }) { Text(stringResource(R.string.aceptar)) }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text(stringResource(R.string.cancelar)) } }
    )
}

/**
 * Rueda vertical: se desliza y se detiene siempre con un valor en el centro (el resaltado).
 * Truco: se agregan 2 espacios vacíos arriba y abajo para que el primer y el último valor también puedan quedar al centro.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Rueda(items: List<String>, indiceInicial: Int, onCambio: (Int) -> Unit) {
    val alto = 44.dp
    val visibles = 5
    val relleno = visibles / 2
    val inicio = indiceInicial.coerceIn(0, items.lastIndex)
    val estado = rememberLazyListState(initialFirstVisibleItemIndex = inicio)
    val fling = rememberSnapFlingBehavior(estado)
    var centrado by remember { mutableStateOf(inicio) }

    LaunchedEffect(estado) {
        snapshotFlow {
            val tam = estado.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 1
            val extra = if (estado.firstVisibleItemScrollOffset > tam / 2) 1 else 0
            (estado.firstVisibleItemIndex + extra).coerceIn(0, items.lastIndex) to estado.isScrollInProgress
        }.collect { (indice, enMovimiento) ->
            centrado = indice
            if (!enMovimiento) onCambio(indice)
        }
    }

    Box(Modifier.width(72.dp).height(alto * visibles), contentAlignment = Alignment.Center) {
        // Franja que marca el valor elegido
        Box(
            Modifier.fillMaxWidth().height(alto)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
        )
        LazyColumn(state = estado, flingBehavior = fling, modifier = Modifier.height(alto * visibles)) {
            val lista = List(relleno) { "" } + items + List(relleno) { "" }
            itemsIndexed(lista) { posicion, texto ->
                val esElegido = posicion - relleno == centrado
                Box(Modifier.fillMaxWidth().height(alto), contentAlignment = Alignment.Center) {
                    Text(
                        texto,
                        style = if (esElegido) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                        fontWeight = if (esElegido) FontWeight.Bold else FontWeight.Normal,
                        color = if (esElegido) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
