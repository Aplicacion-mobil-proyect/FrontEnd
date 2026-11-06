package com.example.frontend.view

import com.example.frontend.view.components.SelectorIdiomaFila
import com.example.frontend.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.model.Roles
import com.example.frontend.navigation.Rutas
import com.example.frontend.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    vm: PerfilViewModel = viewModel()
) {
    var confirmar by remember { mutableStateOf(false) }
    val usuario = vm.usuario

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mi_perfil)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (usuario == null) {
                Text(stringResource(R.string.sin_sesion), style = MaterialTheme.typography.bodyLarge)
                Button(onClick = {
                    navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                }) { Text(stringResource(R.string.ir_a_login)) }
            } else {
                Icon(Icons.Filled.AccountCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(88.dp))
                Text(usuario.nombre, style = MaterialTheme.typography.headlineSmall)
                Text(usuario.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    when (usuario.rol) {
                        Roles.TURISTA -> stringResource(R.string.rol_turista)
                        Roles.COLEGIO -> stringResource(R.string.rol_colegio)
                        Roles.ENCARGADO -> stringResource(R.string.rol_encargado)
                        else -> usuario.rol
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                SelectorIdiomaFila(Modifier.padding(top = 16.dp))
                Button(onClick = { confirmar = true }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                    Text(stringResource(R.string.cerrar_sesion))
                }
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text(stringResource(R.string.cerrar_sesion)) },
            text = { Text(stringResource(R.string.confirmar_cerrar_sesion)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    vm.cerrarSesion()
                    // Borra todo el historial para que "atrás" no vuelva a una pantalla con sesión
                    navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                }) { Text(stringResource(R.string.salir)) }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text(stringResource(R.string.cancelar)) } }
        )
    }
}
