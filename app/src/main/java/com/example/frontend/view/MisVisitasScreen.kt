package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.model.EstadosVisita
import com.example.frontend.model.VisitaDetalle
import com.example.frontend.util.FechasUtil
import com.example.frontend.view.components.PatConfirmDialog
import com.example.frontend.view.components.PatMensajeCentral
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.MisVisitasViewModel

@Composable
fun MisVisitasScreen(navController: NavController, vm: MisVisitasViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    var porCancelar by remember { mutableStateOf<VisitaDetalle?>(null) }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    PatScaffold("Mis visitas", snack, onVolver = { navController.popBackStack() }) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { lista ->
            if (lista.isEmpty()) {
                PatMensajeCentral("No tienes visitas agendadas.\nAgenda una desde la ficha de un recinto.")
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(lista, key = { it.id }) { v ->
                        val cancelada = v.estado == EstadosVisita.CANCELADA
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(v.recintoNombre, style = MaterialTheme.typography.titleMedium)
                                Text("${FechasUtil.isoALegible(v.fecha)} · ${v.hora}", style = MaterialTheme.typography.bodyMedium)
                                Text("${v.personas} persona(s)", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    if (v.conGuia) "Guía: ${v.guiaNombre ?: "por asignar"}" else "Sin guía",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    if (cancelada) "Cancelada" else "Agendada",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (cancelada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                if (!cancelada) {
                                    OutlinedButton(onClick = { porCancelar = v }) { Text("Cancelar visita") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    porCancelar?.let { v ->
        PatConfirmDialog(
            titulo = "Cancelar visita",
            texto = "¿Cancelar tu visita a ${v.recintoNombre} el ${FechasUtil.isoALegible(v.fecha)}?",
            textoConfirmar = "Sí, cancelar",
            onConfirmar = { vm.cancelar(v.id); porCancelar = null },
            onCancelar = { porCancelar = null }
        )
    }
}
