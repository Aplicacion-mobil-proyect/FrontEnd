package com.example.frontend.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import com.example.frontend.util.FechasUtil
import com.example.frontend.view.components.PatCampo
import com.example.frontend.view.components.PatMensajeCentral
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.viewmodel.GuiasViewModel
import com.example.frontend.viewmodel.MSG_SIN_RECINTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiasScreen(navController: NavController, vm: GuiasViewModel = viewModel()) {
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    val guias by vm.guias.collectAsState(initial = emptyList())
    val bloques by vm.bloques.collectAsState(initial = emptyList())

    PatScaffold("Disponibilidad de guías", snack, onVolver = { navController.popBackStack() }) { padding ->
        if (vm.recintoId == null) {
            PatMensajeCentral(MSG_SIN_RECINTO, Modifier.padding(padding), esError = true)
            return@PatScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- Formulario para agregar un bloque ----------
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Agregar bloque de disponibilidad", style = MaterialTheme.typography.titleMedium)

                        Text("Guía", style = MaterialTheme.typography.labelLarge)
                        Row(
                            Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            guias.forEach { guia ->
                                FilterChip(
                                    selected = vm.guiaSeleccionada == guia.id,
                                    onClick = { vm.seleccionarGuia(guia.id) },
                                    label = { Text(guia.nombre) }
                                )
                            }
                        }

                        Text("Día", style = MaterialTheme.typography.labelLarge)
                        Row(
                            Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (1..7).forEach { dia ->
                                FilterChip(
                                    selected = vm.diaSeleccionado == dia,
                                    onClick = { vm.seleccionarDia(dia) },
                                    label = { Text(FechasUtil.nombreDia(dia).take(3)) }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PatCampo(vm.inicio, { vm.cambiarInicio(it) }, "Desde", null,
                                modifier = Modifier.weight(1f), placeholder = "10:00")
                            PatCampo(vm.fin, { vm.cambiarFin(it) }, "Hasta", null,
                                modifier = Modifier.weight(1f), placeholder = "14:00")
                        }

                        if (vm.error != null) {
                            Text(vm.error!!, color = MaterialTheme.colorScheme.error)
                        }

                        Button(onClick = { vm.agregar(bloques) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Agregar bloque")
                        }
                    }
                }
            }

            // ---------- Bloques ya registrados ----------
            item {
                Text("Bloques registrados", style = MaterialTheme.typography.titleMedium)
            }

            if (bloques.isEmpty()) {
                item {
                    Text(
                        "Todavía no hay bloques. Sin bloques, los visitantes no podrán pedir guía.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(bloques) { bloque ->
                val nombreGuia = guias.find { it.id == bloque.guiaId }?.nombre ?: "Guía #${bloque.guiaId}"
                Card(Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(nombreGuia) },
                        supportingContent = {
                            Text("${FechasUtil.nombreDia(bloque.dia)} · ${bloque.inicio} – ${bloque.fin}")
                        },
                        trailingContent = {
                            IconButton(onClick = { vm.eliminar(bloque) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar bloque")
                            }
                        }
                    )
                }
            }
        }
    }
}
