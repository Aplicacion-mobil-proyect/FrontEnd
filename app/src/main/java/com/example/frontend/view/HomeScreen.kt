package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend.model.Roles
import com.example.frontend.navigation.Rutas
import com.example.frontend.session.Sesion

/*
 * HOME TEMPORAL: menú para probar todas las pantallas mientras se desarrollan.
 * El compañero la reemplaza por el diseño final (puede reutilizar estas listas).
 */

data class OpcionMenu(val titulo: String, val icono: ImageVector, val ruta: String)

val opcionesVisitante = listOf(
    OpcionMenu("Mapa patrimonial", Icons.Filled.Map, Rutas.MAPA),
    OpcionMenu("Recintos cercanos", Icons.Filled.NearMe, Rutas.CERCANOS),
    OpcionMenu("Escanear QR", Icons.Filled.QrCodeScanner, Rutas.ESCANER_QR),
    OpcionMenu("Eventos", Icons.Filled.Celebration, Rutas.EVENTOS),
    OpcionMenu("Mis rutas", Icons.Filled.Directions, Rutas.MIS_RUTAS),
    OpcionMenu("Favoritos", Icons.Filled.Favorite, Rutas.FAVORITOS),
    OpcionMenu("Mis visitas", Icons.Filled.EventAvailable, Rutas.MIS_VISITAS),
    OpcionMenu("Perfil", Icons.Filled.Person, Rutas.PERFIL)
)

val opcionesEncargado = listOf(
    OpcionMenu("Mi recinto", Icons.Filled.Store, Rutas.ENC_MI_RECINTO),
    OpcionMenu("Cambiar estado", Icons.Filled.ToggleOn, Rutas.ENC_ESTADO),
    OpcionMenu("Editar horarios", Icons.Filled.AccessTime, Rutas.ENC_HORARIOS),
    OpcionMenu("Eventos del recinto", Icons.Filled.Event, Rutas.ENC_EVENTOS),
    OpcionMenu("Disponibilidad de guías", Icons.Filled.Groups, Rutas.ENC_GUIAS),
    OpcionMenu("Perfil", Icons.Filled.Badge, Rutas.PERFIL)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val usuario = Sesion.obtener(context)
    val opciones = if (usuario?.rol == Roles.ENCARGADO) opcionesEncargado else opcionesVisitante

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola, ${usuario?.nombre ?: ""}") },
                actions = {
                    IconButton(onClick = {
                        Sesion.cerrar(context)
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.HOME) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            items(opciones) { opcion ->
                ElevatedCard(
                    onClick = { navController.navigate(opcion.ruta) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(opcion.titulo) },
                        leadingContent = {
                            Icon(opcion.icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )
                }
            }
        }
    }
}
