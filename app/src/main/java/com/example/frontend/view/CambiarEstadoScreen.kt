package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.util.etiquetaEstadoRecinto
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.CambiarEstadoViewModel

@Composable
fun CambiarEstadoScreen(navController: NavController, vm: CambiarEstadoViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    LaunchedEffect(vm.guardado) {
        if (vm.guardado) navController.popBackStack()
    }

    PatScaffold("Cambiar estado", snack, onVolver = { navController.popBackStack() }) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { recinto ->
            val elegido = vm.seleccion ?: recinto.estado
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(recinto.nombre, style = MaterialTheme.typography.titleLarge)
                Text("Estado actual: ${etiquetaEstadoRecinto(recinto.estado)}", style = MaterialTheme.typography.bodyMedium)
                Column(Modifier.selectableGroup().padding(top = 8.dp)) {
                    EstadosRecinto.todos.forEach { estado ->
                        Row(
                            Modifier.fillMaxWidth()
                                .selectable(selected = elegido == estado, onClick = { vm.seleccionar(estado) }, role = Role.RadioButton)
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = elegido == estado, onClick = null)
                            Text(etiquetaEstadoRecinto(estado), modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                }
                Button(
                    onClick = { vm.guardar(recinto.estado) },
                    enabled = !vm.guardando,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) { Text("Guardar estado") }
            }
        }
    }
}
