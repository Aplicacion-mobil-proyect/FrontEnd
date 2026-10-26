package com.example.frontend.view

import androidx.compose.foundation.layout.aspectRatio
import com.example.frontend.view.components.ImagenRecinto
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Rutas
import com.example.frontend.view.components.PatCampo
import com.example.frontend.view.components.PatEstadoChip
import com.example.frontend.view.components.PatHorarios
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.PlanificarVisitaViewModel

@Composable
fun PlanificarVisitaScreen(navController: NavController, vm: PlanificarVisitaViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    // Visita agendada: va a "Mis visitas" y saca este formulario del historial
    LaunchedEffect(vm.agendada) {
        if (vm.agendada) {
            navController.navigate(Rutas.MIS_VISITAS) {
                popUpTo(Rutas.PLANIFICAR_VISITA) { inclusive = true }
            }
        }
    }

    PatScaffold("Planificar visita", snack, onVolver = { navController.popBackStack() }) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { data ->
            val r = data.recinto
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 12.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ImagenRecinto(r.id, r.nombre, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
                Text(r.nombre, style = MaterialTheme.typography.titleLarge)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PatEstadoChip(r.estado)
                    Text("Visita de ${r.duracionVisita} min", style = MaterialTheme.typography.bodySmall)
                }

                Text("Horario de atención", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
                PatHorarios(data.horarios)

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                PatCampo(vm.fecha, vm::cambiarFecha, "Fecha", vm.errorFecha, teclado = KeyboardType.Number, placeholder = "dd/mm/aaaa")
                PatCampo(vm.hora, vm::cambiarHora, "Hora de llegada", vm.errorHora, placeholder = "HH:mm")
                PatCampo(
                    vm.personas, vm::cambiarPersonas,
                    "Cantidad de personas (${vm.minPersonas} a ${vm.maxPersonas})",
                    vm.errorPersonas, teclado = KeyboardType.Number
                )

                Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Quiero un guía", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = vm.conGuia, onCheckedChange = { vm.cambiarConGuia(it) })
                }

                if (vm.conGuia) {
                    when {
                        vm.buscandoGuias -> CircularProgressIndicator(Modifier.padding(8.dp))
                        vm.guias.isNotEmpty() -> Column(Modifier.selectableGroup()) {
                            Text("Guías disponibles", style = MaterialTheme.typography.labelLarge)
                            vm.guias.forEach { g ->
                                Row(
                                    Modifier.fillMaxWidth()
                                        .selectable(selected = vm.guiaId == g.id, onClick = { vm.elegirGuia(g.id) }, role = Role.RadioButton)
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = vm.guiaId == g.id, onClick = null)
                                    Column(Modifier.padding(start = 12.dp)) {
                                        Text(g.nombre, style = MaterialTheme.typography.bodyLarge)
                                        Text(g.idiomas, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                        else -> Text(
                            "Escribe una fecha y una hora válidas para ver los guías disponibles.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    vm.errorGuia?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }

                Button(
                    onClick = { vm.agendar(data) },
                    enabled = !vm.guardando,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
                ) {
                    if (vm.guardando) CircularProgressIndicator(Modifier.padding(2.dp)) else Text("Agendar visita")
                }
            }
        }
    }
}
