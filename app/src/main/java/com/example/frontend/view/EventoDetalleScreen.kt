package com.example.frontend.view

import androidx.compose.foundation.layout.aspectRatio
import com.example.frontend.view.components.ImagenRecinto
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Destinos
import com.example.frontend.util.FechasUtil
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.EventoDetalleViewModel
import com.example.frontend.viewmodel.UiState

@Composable
fun EventoDetalleScreen(navController: NavController, vm: EventoDetalleViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val esFavorito by vm.esFavorito.collectAsState()
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    PatScaffold(
        titulo = "Detalle del evento",
        snackbarHostState = snack,
        onVolver = { navController.popBackStack() },
        acciones = {
            // Corazón: marca/desmarca el recinto del evento como favorito
            val evento = (state as? UiState.Success)?.data
            if (evento != null) {
                IconButton(onClick = { vm.alternarFavorito(evento.recintoId) }) {
                    Icon(
                        if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (esFavorito) "Quitar recinto de favoritos" else "Guardar recinto en favoritos",
                        tint = if (esFavorito) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { e ->
            Column(
                Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImagenRecinto(e.recintoId, e.recintoNombre, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
                Text(e.tipo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(e.titulo, style = MaterialTheme.typography.headlineSmall)
                Text("Lugar: ${e.recintoNombre}", style = MaterialTheme.typography.bodyLarge)
                Text("${FechasUtil.isoALegible(e.fecha)}, ${e.hora}", style = MaterialTheme.typography.bodyMedium)
                Text(e.descripcion, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
                OutlinedButton(
                    onClick = { navController.navigate(Destinos.ficha(e.recintoId)) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) { Text("Ver ficha del recinto") }
            }
        }
    }
}
