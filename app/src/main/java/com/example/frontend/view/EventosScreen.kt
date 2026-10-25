package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.example.frontend.navigation.Destinos
import com.example.frontend.util.FechasUtil
import com.example.frontend.util.TIPOS_EVENTO
import com.example.frontend.view.components.PatMensajeCentral
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.EventosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(navController: NavController, vm: EventosViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val tipo by vm.tipo.collectAsState()
    val snack = remember { SnackbarHostState() }

    PatScaffold("Eventos", snack, onVolver = { navController.popBackStack() }) { padding ->
        Column(Modifier.padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { FilterChip(selected = tipo == null, onClick = { vm.seleccionarTipo(null) }, label = { Text("Todos") }) }
                items(TIPOS_EVENTO) { t ->
                    FilterChip(selected = tipo == t, onClick = { vm.seleccionarTipo(t) }, label = { Text(t) })
                }
            }
            PatUiContent(state) { lista ->
                if (lista.isEmpty()) {
                    PatMensajeCentral("No hay eventos próximos de este tipo")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lista, key = { it.id }) { e ->
                            Card(
                                onClick = { navController.navigate(Destinos.eventoDetalle(e.id)) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(e.tipo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(e.titulo, style = MaterialTheme.typography.titleMedium)
                                    Text(e.recintoNombre, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${FechasUtil.isoALegible(e.fecha)} · ${e.hora}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
