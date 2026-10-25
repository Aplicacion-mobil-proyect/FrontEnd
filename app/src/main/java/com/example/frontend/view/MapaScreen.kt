package com.example.frontend.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.model.Recinto
import com.example.frontend.navigation.Destinos
import com.example.frontend.util.UbicacionUtil
import com.example.frontend.view.components.PatEstadoChip
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.MapaViewModel
import kotlin.math.hypot

// Área que cubre el mapa esquemático: centro de Santiago, Recoleta y Providencia
private const val LAT_MIN = -33.455
private const val LAT_MAX = -33.418
private const val LNG_MIN = -70.670
private const val LNG_MAX = -70.622

private fun Recinto.dentroDelMapa() = latitud in LAT_MIN..LAT_MAX && longitud in LNG_MIN..LNG_MAX

private fun colorEstado(estado: String) = when (estado) {
    EstadosRecinto.ABIERTO -> Color(0xFF2E7D32)
    EstadosRecinto.CERRADO -> Color(0xFFC62828)
    EstadosRecinto.EN_CELEBRACION -> Color(0xFFEF9A00)
    else -> Color.Gray
}

@OptIn(ExperimentalMaterial3Api::class)
/** Mapa esquemático: cada recinto es un punto ubicado según su latitud y longitud. Toca un punto para ver el recinto. */
@Composable
fun MapaScreen(navController: NavController, vm: MapaViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    var seleccionado by remember { mutableStateOf<Recinto?>(null) }

    PatScaffold("Mapa patrimonial", snack, onVolver = { navController.popBackStack() }) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { recintos ->
            val dentro = recintos.filter { it.dentroDelMapa() }
            val fuera = recintos.filterNot { it.dentroDelMapa() }

            Column(
                Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MapaEsquematico(dentro, seleccionado) { seleccionado = it }

                // Leyenda
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Leyenda(colorEstado(EstadosRecinto.ABIERTO), "Abierto")
                    Leyenda(colorEstado(EstadosRecinto.CERRADO), "Cerrado")
                    Leyenda(colorEstado(EstadosRecinto.EN_CELEBRACION), "En celebración")
                }
                Text("Mapa esquemático del centro de Santiago. Toca un punto para ver el recinto.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                seleccionado?.let { r ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(r.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${r.tipo} · ${r.comuna}", style = MaterialTheme.typography.bodyMedium)
                            PatEstadoChip(r.estado)
                            Button(onClick = { navController.navigate(Destinos.ficha(r.id)) }) { Text("Ver ficha") }
                        }
                    }
                }

                if (fuera.isNotEmpty()) {
                    Text("Fuera del área del mapa", style = MaterialTheme.typography.titleSmall)
                    fuera.forEach { r ->
                        Card(onClick = { navController.navigate(Destinos.ficha(r.id)) }, modifier = Modifier.fillMaxWidth()) {
                            Text("${r.nombre} (${r.comuna})", Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Leyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("● ", color = color, style = MaterialTheme.typography.bodyMedium)
        Text(texto, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MapaEsquematico(recintos: List<Recinto>, seleccionado: Recinto?, onSeleccion: (Recinto) -> Unit) {
    var tam by remember { mutableStateOf(IntSize.Zero) }
    val fondo = Color(0xFFEAF0E4)
    val linea = Color(0xFFCBD5C0)
    val centroColor = MaterialTheme.colorScheme.primary

    // Posición en pantalla de una coordenada
    fun posicion(lat: Double, lng: Double) = Offset(
        x = ((lng - LNG_MIN) / (LNG_MAX - LNG_MIN) * tam.width).toFloat(),
        y = ((LAT_MAX - lat) / (LAT_MAX - LAT_MIN) * tam.height).toFloat()
    )

    Canvas(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onSizeChanged { tam = it }
            .pointerInput(recintos, tam) {
                detectTapGestures { toque ->
                    // El punto más cercano al toque (máximo 40 px de distancia)
                    val cercano = recintos.minByOrNull {
                        val p = posicion(it.latitud, it.longitud)
                        hypot(p.x - toque.x, p.y - toque.y)
                    }
                    if (cercano != null) {
                        val p = posicion(cercano.latitud, cercano.longitud)
                        if (hypot(p.x - toque.x, p.y - toque.y) <= 40f * density) onSeleccion(cercano)
                    }
                }
            }
    ) {
        drawRect(fondo)
        // Cuadrícula de referencia
        for (i in 1..5) {
            val x = size.width * i / 6f
            val y = size.height * i / 6f
            drawLine(linea, Offset(x, 0f), Offset(x, size.height), strokeWidth = 2f)
            drawLine(linea, Offset(0f, y), Offset(size.width, y), strokeWidth = 2f)
        }
        // Plaza de Armas como punto de referencia
        val plaza = posicion(UbicacionUtil.PLAZA_LAT, UbicacionUtil.PLAZA_LNG)
        drawCircle(centroColor, radius = 7f * density, center = plaza, style = Stroke(width = 2f * density))

        recintos.forEach { r ->
            val p = posicion(r.latitud, r.longitud)
            val sel = r.id == seleccionado?.id
            if (sel) drawCircle(Color.Black.copy(alpha = 0.25f), radius = 18f * density, center = p)
            drawCircle(colorEstado(r.estado), radius = (if (sel) 11f else 8f) * density, center = p)
            drawCircle(Color.White, radius = (if (sel) 11f else 8f) * density, center = p, style = Stroke(width = 2f * density))
        }
    }
}
