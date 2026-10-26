package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Rutas
import com.example.frontend.view.components.PatCampo
import com.example.frontend.view.components.PatEstadoChip
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.viewmodel.PlanificarVisitaViewModel

@Composable
fun PlanificarVisitaScreen(
    navController: NavController,
    recintoId: Int,
    vm: PlanificarVisitaViewModel = viewModel()
) {
    val snack = remember { SnackbarHostState() }

    // Carga el recinto una sola vez al abrir la pantalla
    LaunchedEffect(recintoId) { vm.cargarRecinto(recintoId) }

    // Cuando se agenda, muestra el mensaje y va a "Mis visitas"
    LaunchedEffect(vm.mensajeExito) {
        val msg = vm.mensajeExito
        if (msg != null) {
            snack.showSnackbar(msg)
            navController.navigate(Rutas.MIS_VISITAS) {
                popUpTo(Rutas.PLANIFICAR_VISITA) { inclusive = true }
            }
        }
    }

    PatScaffold("Planificar visita", snack, onVolver = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Recinto elegido
            val r = vm.recinto
            if (r != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(r.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("${r.direccion}, ${r.comuna}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        PatEstadoChip(r.estado)
                    }
                }
            }

            Text("Datos de la visita", style = MaterialTheme.typography.titleMedium)

            PatCampo(
                valor = vm.fecha,
                onCambio = { vm.cambiarFecha(it) },
                etiqueta = "Fecha",
                error = null,
                placeholder = "dd/mm/aaaa"
            )
            PatCampo(
                valor = vm.hora,
                onCambio = { vm.cambiarHora(it) },
                etiqueta = "Hora",
                error = null,
                placeholder = "HH:mm (ej. 10:30)"
            )
            PatCampo(
                valor = vm.personas,
                onCambio = { vm.cambiarPersonas(it) },
                etiqueta = "Cantidad de personas (máx. ${vm.maxPersonas})",
                error = null,
                teclado = KeyboardType.Number
            )

            // Guía
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Solicitar guía", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Se asigna automáticamente un guía disponible",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = vm.conGuia, onCheckedChange = { vm.cambiarConGuia(it) })
            }

            // Error de validación
            if (vm.error != null) {
                Text(vm.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { vm.agendar() },
                enabled = !vm.guardando && vm.recinto != null,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (vm.guardando) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text("Agendar visita")
                }
            }
        }
    }
}
