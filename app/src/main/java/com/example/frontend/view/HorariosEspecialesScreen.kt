package com.example.frontend.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.frontend.viewmodel.HorariosEspecialesViewModel
import com.example.frontend.viewmodel.MSG_SIN_RECINTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorariosEspecialesScreen(navController: NavController, vm: HorariosEspecialesViewModel = viewModel()) {
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    val especiales by vm.especiales.collectAsState(initial = emptyList())

    PatScaffold("Horarios especiales", snack, onVolver = { navController.popBackStack() }) { padding ->
        if (vm.recintoId == null) {
            PatMensajeCentral(MSG_SIN_RECINTO, Modifier.padding(padding), esError = true)
            return@PatScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- Formulario ----------
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Informar apertura excepcional", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Para un cierre excepcional usa \"Cambiar estado\" → Cerrado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        PatCampo(vm.descripcion, { vm.cambiarDescripcion(it) }, "Motivo", null,
                            placeholder = "Ej. Vigilia de Semana Santa")

                        Text("Día", style = MaterialTheme.typography.labelLarge)
                        Row(
                            Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (1..7).forEach { d ->
                                FilterChip(
                                    selected = vm.dia == d,
                                    onClick = { vm.cambiarDia(d) },
                                    label = { Text(FechasUtil.nombreDia(d).take(3)) }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PatCampo(vm.apertura, { vm.cambiarApertura(it) }, "Apertura", null,
                                modifier = Modifier.weight(1f), placeholder = "20:00")
                            PatCampo(vm.cierre, { vm.cambiarCierre(it) }, "Cierre", null,
                                modifier = Modifier.weight(1f), placeholder = "23:00")
                        }

                        if (vm.error != null) {
                            Text(vm.error!!, color = MaterialTheme.colorScheme.error)
                        }

                        Button(onClick = { vm.agregar() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Publicar horario especial")
                        }
                    }
                }
            }

            // ---------- Lista ----------
            item { Text("Horarios especiales publicados", style = MaterialTheme.typography.titleMedium) }

            if (especiales.isEmpty()) {
                item {
                    Text("No hay horarios especiales.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            items(especiales) { h ->
                Card(Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(h.descripcion.ifBlank { "Horario especial" }) },
                        supportingContent = { Text("${FechasUtil.nombreDia(h.dia)} · ${h.apertura} – ${h.cierre}") },
                        trailingContent = {
                            IconButton(onClick = { vm.eliminar(h) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar horario especial")
                            }
                        }
                    )
                }
            }
        }
    }
}
