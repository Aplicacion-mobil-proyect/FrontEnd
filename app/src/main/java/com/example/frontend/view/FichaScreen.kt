package com.example.frontend.view

import androidx.compose.foundation.layout.aspectRatio
import com.example.frontend.view.components.ImagenRecinto
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.navigation.Destinos
import com.example.frontend.util.FechasUtil
import com.example.frontend.view.components.PatEstadoChip
import com.example.frontend.view.components.PatHorarios
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.FichaViewModel
import com.example.frontend.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FichaScreen(navController: NavController, vm: FichaViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val esFavorito by vm.esFavorito.collectAsState()
    val snack = remember { SnackbarHostState() }
    val context = LocalContext.current
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    PatScaffold(
        titulo = "Ficha patrimonial",
        snackbarHostState = snack,
        onVolver = { navController.popBackStack() },
        acciones = {
            if (state is UiState.Success) {
                IconButton(onClick = { vm.alternarFavorito() }) {
                    Icon(
                        if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (esFavorito) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { data ->
            val r = data.recinto
            val cerrado = r.estado == EstadosRecinto.CERRADO
            Column(
                Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImagenRecinto(r.id, r.nombre, Modifier.fillMaxWidth().aspectRatio(4f / 3f))
                Text(r.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("${r.tipo} · ${r.comuna}", style = MaterialTheme.typography.bodyLarge)
                PatEstadoChip(r.estado)
                Text(r.direccion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Duración estimada de la visita: ${r.duracionVisita} min", style = MaterialTheme.typography.bodyMedium)

                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        // Abre la app de mapas del teléfono en la ubicación del recinto
                        val uri = Uri.parse("geo:${r.latitud},${r.longitud}?q=${r.latitud},${r.longitud}(${Uri.encode(r.nombre)})")
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } catch (e: ActivityNotFoundException) {
                            // No hay app de mapas instalada
                        }
                    }, modifier = Modifier.weight(1f)) { Text("Cómo llegar") }

                    Button(
                        onClick = { navController.navigate(Destinos.planificarVisita(r.id)) },
                        enabled = !cerrado,
                        modifier = Modifier.weight(1f)
                    ) { Text("Planificar visita") }
                }
                if (cerrado) {
                    Text(
                        "Este recinto está cerrado: por ahora no se pueden agendar visitas.",
                        color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall
                    )
                }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Reseña histórica", style = MaterialTheme.typography.titleMedium)
                Text(r.descripcion, style = MaterialTheme.typography.bodyLarge)

                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Horarios", style = MaterialTheme.typography.titleMedium)
                PatHorarios(data.horarios)

                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Próximos eventos", style = MaterialTheme.typography.titleMedium)
                if (data.eventos.isEmpty()) {
                    Text("No hay eventos próximos en este recinto.", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    data.eventos.forEach { e ->
                        Card(
                            onClick = { navController.navigate(Destinos.eventoDetalle(e.id)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(e.tipo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(e.titulo, style = MaterialTheme.typography.titleSmall)
                                Text("${FechasUtil.isoALegible(e.fecha)} · ${e.hora}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
