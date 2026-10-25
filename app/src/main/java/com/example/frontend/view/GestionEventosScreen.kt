package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
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
import com.example.frontend.model.Evento
import com.example.frontend.navigation.Destinos
import com.example.frontend.util.FechasUtil
import com.example.frontend.view.components.PatConfirmDialog
import com.example.frontend.view.components.PatMensajeCentral
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.GestionEventosViewModel

@Composable
fun GestionEventosScreen(navController: NavController, vm: GestionEventosViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    var porEliminar by remember { mutableStateOf<Evento?>(null) }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    PatScaffold(
        titulo = "Eventos de mi recinto",
        snackbarHostState = snack,
        onVolver = { navController.popBackStack() },
        fab = {
            FloatingActionButton(onClick = { navController.navigate(Destinos.eventoForm(0)) }) {
                Icon(Icons.Filled.Add, contentDescription = "Crear evento")
            }
        }
    ) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { lista ->
            if (lista.isEmpty()) {
                PatMensajeCentral("Aún no has creado eventos.\nToca + para crear el primero.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp), // espacio para que el botón + no tape el último
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista, key = { it.id }) { e ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(e.tipo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(e.titulo, style = MaterialTheme.typography.titleMedium)
                                    Text("${FechasUtil.isoADdMmYyyy(e.fecha)} · ${e.hora}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { navController.navigate(Destinos.eventoForm(e.id)) }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar ${e.titulo}")
                                }
                                IconButton(onClick = { porEliminar = e }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar ${e.titulo}", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    porEliminar?.let { e ->
        PatConfirmDialog(
            titulo = "Eliminar evento",
            texto = "¿Eliminar \"${e.titulo}\"? Esta acción no se puede deshacer.",
            textoConfirmar = "Eliminar",
            onConfirmar = { vm.eliminar(e.id); porEliminar = null },
            onCancelar = { porEliminar = null }
        )
    }
}
