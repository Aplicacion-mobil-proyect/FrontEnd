package com.example.frontend.view

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
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                Text("No hay una sesión activa", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = {
                    navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                }) { Text("Ir a iniciar sesión") }
            } else {
                Icon(Icons.Filled.AccountCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(88.dp))
                Text(usuario.nombre, style = MaterialTheme.typography.headlineSmall)
                Text(usuario.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    when (usuario.rol) {
                        Roles.TURISTA -> "Turista"
                        Roles.COLEGIO -> "Colegio"
                        Roles.ENCARGADO -> "Encargado de recinto"
                        else -> usuario.rol
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(onClick = { confirmar = true }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                    Text("Cerrar sesión")
                }
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    vm.cerrarSesion()
                    // Borra todo el historial para que "atrás" no vuelva a una pantalla con sesión
                    navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                }) { Text("Salir") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
        )
    }
}
