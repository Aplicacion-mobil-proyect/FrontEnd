package com.example.frontend.view

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Destinos
import com.example.frontend.util.UbicacionUtil
import com.example.frontend.view.components.PatEstadoChip
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.CercanosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CercanosScreen(navController: NavController, vm: CercanosViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val referencia by vm.referencia.collectAsState()
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    // Pide el permiso de ubicación; si lo conceden, usa la ubicación real
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { r ->
        if (r.values.any { it }) vm.usarUbicacionReal() else vm.permisoDenegado()
    }

    PatScaffold("Recintos cercanos", snack, onVolver = { navController.popBackStack() }) { padding ->
        Column(Modifier.padding(padding)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (referencia.esUbicacionReal) "Distancias desde tu ubicación"
                    else "Distancias desde la Plaza de Armas (centro de Santiago)",
                    style = MaterialTheme.typography.titleSmall
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            pedirPermiso.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Usar mi ubicación") }
                    OutlinedButton(onClick = { vm.usarPlazaDeArmas() }, modifier = Modifier.weight(1f)) {
                        Text("Plaza de Armas")
                    }
                }
            }

            PatUiContent(state) { lista ->
                // Si el primer recinto está a más de 50 km, la ubicación casi seguro no es Santiago (ej. emulador)
                val lejos = lista.firstOrNull()?.let { it.distanciaKm > 50 } == true
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (lejos) item {
                        Text(
                            "Tu ubicación parece estar lejos de Santiago (¿emulador?). Usa \"Plaza de Armas\" para ver las distancias reales.",
                            color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall
                        )
                    }
                    items(lista, key = { it.recinto.id }) { c ->
                        Card(
                            onClick = { navController.navigate(Destinos.ficha(c.recinto.id)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(c.recinto.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text("${c.recinto.tipo} · ${c.recinto.comuna}", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    PatEstadoChip(c.recinto.estado)
                                }
                                Text(
                                    UbicacionUtil.formatear(c.distanciaKm),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
