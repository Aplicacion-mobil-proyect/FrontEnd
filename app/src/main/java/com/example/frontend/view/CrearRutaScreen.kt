package com.example.frontend.view

import com.example.frontend.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.view.components.PatCampo
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.CrearRutaViewModel

@Composable
fun CrearRutaScreen(navController: NavController, vm: CrearRutaViewModel = viewModel()) {
    val state by vm.recintos.collectAsState()
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    LaunchedEffect(vm.guardado) {
        if (vm.guardado) navController.popBackStack()
    }

    PatScaffold(stringResource(R.string.crear_ruta), snack, onVolver = { navController.popBackStack() }) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { recintos ->
            Column(Modifier.padding(horizontal = 16.dp)) {
                PatCampo(vm.nombre, vm::cambiarNombre, stringResource(R.string.nombre_ruta), vm.errorNombre, Modifier.padding(top = 8.dp), placeholder = stringResource(R.string.ejemplo_nombre_ruta))

                Text(
                    stringResource(R.string.instrucciones_ruta, CrearRutaViewModel.MIN_PARADAS, CrearRutaViewModel.MAX_PARADAS),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                vm.errorParadas?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

                LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(vertical = 4.dp)) {
                    items(recintos, key = { it.id }) { r ->
                        val orden = vm.seleccion.indexOf(r.id) + 1   // 0 si no está elegido
                        Row(
                            Modifier.fillMaxWidth().clickable { vm.alternar(r.id) }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = orden > 0, onCheckedChange = { vm.alternar(r.id) })
                            Column(Modifier.weight(1f)) {
                                Text(r.nombre, style = MaterialTheme.typography.bodyLarge)
                                Text(stringResource(R.string.comuna_duracion, r.comuna, r.duracionVisita), style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (orden > 0) Text(stringResource(R.string.parada_n, orden), style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }

                Text(
                    stringResource(R.string.resumen_ruta, vm.seleccion.size, formatearMinutos(vm.duracionEstimada(recintos))),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { vm.guardar(recintos) },
                    enabled = !vm.guardando,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    if (vm.guardando) CircularProgressIndicator(Modifier.padding(2.dp)) else Text(stringResource(R.string.guardar_ruta))
                }
            }
        }
    }
}
