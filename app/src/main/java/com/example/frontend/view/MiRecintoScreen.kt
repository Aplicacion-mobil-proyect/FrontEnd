package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.frontend.model.Horario
import com.example.frontend.navigation.Rutas
import com.example.frontend.util.FechasUtil
import com.example.frontend.view.components.PatEstadoChip
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.view.components.PatUiContent
import com.example.frontend.viewmodel.MiRecintoViewModel

@Composable
fun MiRecintoScreen(navController: NavController, vm: MiRecintoViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val snack = remember { SnackbarHostState() }

    // Es la pantalla de inicio del encargado: sin flecha atrás, con acceso a su perfil (y cerrar sesión)
    PatScaffold(
        titulo = "Mi recinto",
        snackbarHostState = snack,
        acciones = {
            IconButton(onClick = { navController.navigate(Rutas.PERFIL) }) {
                Icon(Icons.Filled.Person, contentDescription = "Mi perfil")
            }
        }
    ) { padding ->
        PatUiContent(state, Modifier.padding(padding)) { data ->
            val r = data.recinto
            Column(
                Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(r.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("${r.tipo} · ${r.comuna}", style = MaterialTheme.typography.bodyMedium)
                Text(r.direccion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                PatEstadoChip(r.estado)
                Text(r.descripcion, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))

                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Horarios", style = MaterialTheme.typography.titleMedium)
                BloqueHorarios(data.horarios)

                Button(onClick = { navController.navigate(Rutas.ENC_ESTADO) }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Text("Cambiar estado")
                }
                OutlinedButton(onClick = { navController.navigate(Rutas.ENC_HORARIOS) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Editar horarios")
                }
                OutlinedButton(onClick = { navController.navigate(Rutas.ENC_EVENTOS) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Gestionar eventos")
                }
                OutlinedButton(onClick = { navController.navigate(Rutas.ENC_GUIAS) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Disponibilidad de guías")
                }
            }
        }
    }
}

@Composable
private fun BloqueHorarios(horarios: List<Horario>) {
    val regulares = horarios.filter { !it.especial }.associateBy { it.dia }
    (1..7).forEach { dia ->
        val h = regulares[dia]
        Text(
            "${FechasUtil.nombreDia(dia)}: " + (if (h != null) "${h.apertura} – ${h.cierre}" else "Cerrado"),
            style = MaterialTheme.typography.bodyMedium
        )
    }
    horarios.filter { it.especial }.forEach {
        Text(
            "${FechasUtil.nombreDia(it.dia)} · ${it.descripcion.ifBlank { "Horario especial" }}: ${it.apertura} – ${it.cierre}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
