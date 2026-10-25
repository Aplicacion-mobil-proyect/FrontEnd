package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.model.Ruta
import com.example.frontend.navigation.Destinos
import com.example.frontend.navigation.Rutas
import com.example.frontend.view.components.PatConfirmDialog
import com.example.frontend.view.components.PatMensajeCentral
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.MisRutasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisRutasScreen(navController: NavController, vm: MisRutasViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    var porEliminar by remember { mutableStateOf<Ruta?>(null) }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    PatScaffold(
        titulo = "Mis rutas",
        snackbarHostState = snack,
        onVolver = { navController.popBackStack() },
        fab = {
            FloatingActionButton(onClick = { navController.navigate(Rutas.CREAR_RUTA) }) {
                Icon(Icons.Filled.Add, contentDescription = "Crear ruta")
            }
        }
    ) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { lista ->
            if (lista.isEmpty()) {
                PatMensajeCentral("Aún no tienes rutas.\nToca + para armar un recorrido por varios recintos.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista, key = { it.id }) { ruta ->
                        Card(
                            onClick = { navController.navigate(Destinos.iniciarRuta(ruta.id)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(ruta.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Duración estimada: ${formatearMinutos(ruta.duracion)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { porEliminar = ruta }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar ${ruta.nombre}", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    porEliminar?.let { ruta ->
        PatConfirmDialog(
            titulo = "Eliminar ruta",
            texto = "¿Eliminar la ruta \"${ruta.nombre}\"?",
            textoConfirmar = "Eliminar",
            onConfirmar = { vm.eliminar(ruta.id); porEliminar = null },
            onCancelar = { porEliminar = null }
        )
    }
}

/** 135 -> "2 h 15 min", 45 -> "45 min" */
fun formatearMinutos(min: Int): String =
    if (min < 60) "$min min" else if (min % 60 == 0) "${min / 60} h" else "${min / 60} h ${min % 60} min"
