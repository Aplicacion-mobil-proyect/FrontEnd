package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.util.FechasUtil
import com.example.frontend.view.components.PatCampo
import com.example.frontend.view.components.PatMensajeCentral
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.viewmodel.EditarHorariosViewModel

@Composable
fun EditarHorariosScreen(navController: NavController, vm: EditarHorariosViewModel = viewModel()) {
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    LaunchedEffect(vm.guardado) {
        if (vm.guardado) navController.popBackStack()
    }

    PatScaffold("Editar horarios", snack, onVolver = { navController.popBackStack() }) { padding ->
        when {
            vm.cargando -> PatMensajeCentral("Cargando horarios…", Modifier.padding(padding))
            vm.errorCarga != null -> PatMensajeCentral(vm.errorCarga!!, Modifier.padding(padding), esError = true)
            else -> Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    itemsIndexed(vm.filas, key = { _, f -> f.dia }) { i, f ->
                        Column(Modifier.padding(vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(FechasUtil.nombreDia(f.dia), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                                Text(if (f.abierto) "Abierto" else "Cerrado", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = f.abierto,
                                    onCheckedChange = { vm.onAbierto(i, it) },
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            if (f.abierto) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    PatCampo(f.apertura, { vm.onApertura(i, it) }, "Apertura", null, Modifier.weight(1f), placeholder = "09:00")
                                    PatCampo(f.cierre, { vm.onCierre(i, it) }, "Cierre", null, Modifier.weight(1f), placeholder = "18:00")
                                }
                                f.error?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = { vm.guardar() },
                    enabled = !vm.guardando,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    if (vm.guardando) CircularProgressIndicator(Modifier.padding(2.dp)) else Text("Guardar horarios")
                }
            }
        }
    }
}
