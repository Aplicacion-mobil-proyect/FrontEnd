package com.example.frontend.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Destinos
import com.example.frontend.view.components.PatEstadoChip
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.IniciarRutaViewModel

@Composable
fun IniciarRutaScreen(navController: NavController, vm: IniciarRutaViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    val context = LocalContext.current

    PatScaffold("Recorrido", snack, onVolver = { navController.popBackStack() }) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { detalle ->
            val total = detalle.paradas.size
            val hechas = detalle.paradas.count { it.id in vm.visitadas }
            // Próxima parada = la primera que aún no se marca como visitada
            val siguiente = detalle.paradas.firstOrNull { it.id !in vm.visitadas }

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(detalle.ruta.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("Duración estimada: ${formatearMinutos(detalle.ruta.duracion)}", style = MaterialTheme.typography.bodyMedium)
                LinearProgressIndicator(
                    progress = { if (total == 0) 0f else hechas.toFloat() / total },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
                Text(
                    if (siguiente == null && total > 0) "¡Recorrido completado!" else "$hechas de $total paradas visitadas",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(detalle.paradas, key = { _, r -> r.id }) { i, r ->
                        val visitada = r.id in vm.visitadas
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = visitada, onCheckedChange = { vm.alternarVisitada(r.id) })
                                    Column(Modifier.weight(1f)) {
                                        Text("${i + 1}. ${r.nombre}", style = MaterialTheme.typography.titleSmall)
                                        Text("${r.comuna} · ${r.duracionVisita} min", style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    PatEstadoChip(r.estado)
                                }
                                Row {
                                    TextButton(onClick = { navController.navigate(Destinos.ficha(r.id)) }) { Text("Ver ficha") }
                                    if (r.id == siguiente?.id) {
                                        TextButton(onClick = {
                                            val uri = Uri.parse("geo:${r.latitud},${r.longitud}?q=${r.latitud},${r.longitud}(${Uri.encode(r.nombre)})")
                                            try {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                            } catch (e: ActivityNotFoundException) { }
                                        }) { Text("Cómo llegar") }
                                    }
                                }
                            }
                        }
                    }
                }
                OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Terminar") }
            }
        }
    }
}
