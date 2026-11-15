package com.example.frontend.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.R
import com.example.frontend.model.Roles
import com.example.frontend.navigation.Rutas
import com.example.frontend.view.components.AvatarUsuario
import com.example.frontend.view.components.SelectorIdiomaFila
import com.example.frontend.view.components.SelectorPaletaFila
import com.example.frontend.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    vm: PerfilViewModel = viewModel()
) {
    var confirmar by remember { mutableStateOf(false) }
    var acercaDe by remember { mutableStateOf(false) }
    val usuario by vm.usuario.collectAsState()
    val actividad by vm.actividad.collectAsState()

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
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val u = usuario
            if (u == null) {
                Text(stringResource(R.string.sin_sesion), style = MaterialTheme.typography.bodyLarge)
                Button(onClick = {
                    navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                }) { Text(stringResource(R.string.ir_a_login)) }
            } else {
                // ---------- Datos del usuario ----------
                AvatarUsuario(u.nombre, u.avatar)
                Text(u.nombre, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                if (!u.apodo.isNullOrBlank()) {
                    Text(
                        stringResource(R.string.apodo_mostrar, u.apodo),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(u.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    when (u.rol) {
                        Roles.TURISTA -> stringResource(R.string.rol_turista)
                        Roles.COLEGIO -> stringResource(R.string.rol_colegio)
                        Roles.ENCARGADO -> stringResource(R.string.rol_encargado)
                        else -> u.rol
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedButton(
                    onClick = { navController.navigate(Rutas.EDITAR_PERFIL) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                    Text(stringResource(R.string.editar_perfil), Modifier.padding(start = 8.dp))
                }

                // ---------- Mi actividad (solo turista y colegio) ----------
                if (u.rol != Roles.ENCARGADO) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.mi_actividad), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                                Contador(actividad.favoritos, stringResource(R.string.contador_favoritos), Modifier.weight(1f))
                                Contador(actividad.visitas, stringResource(R.string.contador_visitas), Modifier.weight(1f))
                                Contador(actividad.rutas, stringResource(R.string.contador_rutas), Modifier.weight(1f))
                            }
                        }
                    }
                }

                // ---------- Ajustes ----------
                Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    SelectorIdiomaFila()
                    HorizontalDivider()
                    SelectorPaletaFila()
                    HorizontalDivider()
                    FilaOpcion(Icons.Outlined.Info, stringResource(R.string.acerca_de)) { acercaDe = true }
                }

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

    if (acercaDe) {
        AlertDialog(
            onDismissRequest = { acercaDe = false },
            title = { Text(stringResource(R.string.acerca_de)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Red Patrimonial de Santiago", fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.acerca_version, "1.0"))
                    Text(stringResource(R.string.acerca_descripcion))
                    Text(stringResource(R.string.acerca_equipo, "Taylor Medolphe · Estefano Monar"))
                    Text("Duoc UC · 2026", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { TextButton(onClick = { acercaDe = false }) { Text(stringResource(R.string.aceptar)) } }
        )
    }
}

/** Número grande con su etiqueta debajo (ej: "3 / Favoritos"). */
@Composable
private fun Contador(valor: Int, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor.toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Fila con ícono, texto y flecha, igual de alta que la fila de idioma. */
@Composable
private fun FilaOpcion(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(texto, Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}
