package com.example.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontend.session.Sesion
import com.example.frontend.view.HomeScreen
import com.example.frontend.view.LoginScreen
import com.example.frontend.view.QrScreen
import com.example.frontend.view.components.EnConstruccion

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Si ya hay sesión guardada parte en Home; si no, en Login
    val inicio = if (Sesion.obtener(context) != null) Rutas.HOME else Rutas.LOGIN

    NavHost(navController = navController, startDestination = inicio) {

        // ----- Acceso -----
        composable(Rutas.SPLASH) { EnConstruccion("Splash", navController) }            // compañero
        composable(Rutas.LOGIN) { LoginScreen(navController) }
        composable(Rutas.REGISTRO) { EnConstruccion("Registro", navController) }        // compañero

        // ----- Turista / colegio -----
        composable(Rutas.HOME) { HomeScreen(navController) }
        composable(Rutas.MAPA) { EnConstruccion("Mapa patrimonial", navController) }    // Taylor
        composable(Rutas.CERCANOS) { EnConstruccion("Recintos cercanos", navController) } // Taylor
        composable(Rutas.ESCANER_QR) { QrScreen(navController) }                          // Taylor

        composable(
            route = Rutas.FICHA,
            arguments = listOf(navArgument("recintoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recintoId = backStackEntry.arguments?.getInt("recintoId") ?: 0
            EnConstruccion("Ficha patrimonial #$recintoId", navController)             // Taylor
        }

        composable(Rutas.EVENTOS) { EnConstruccion("Eventos", navController) }          // compañero

        composable(
            route = Rutas.EVENTO_DETALLE,
            arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
        ) { EnConstruccion("Detalle de evento", navController) }                         // compañero

        composable(Rutas.FAVORITOS) { EnConstruccion("Favoritos", navController) }      // compañero
        composable(Rutas.MIS_VISITAS) { EnConstruccion("Mis visitas", navController) }  // compañero

        composable(
            route = Rutas.PLANIFICAR_VISITA,
            arguments = listOf(navArgument("recintoId") { type = NavType.IntType })
        ) { EnConstruccion("Planificar visita", navController) }                         // Taylor

        composable(Rutas.MIS_RUTAS) { EnConstruccion("Mis rutas", navController) }      // Taylor
        composable(Rutas.CREAR_RUTA) { EnConstruccion("Crear ruta", navController) }    // Taylor

        composable(
            route = Rutas.INICIAR_RUTA,
            arguments = listOf(navArgument("rutaId") { type = NavType.IntType })
        ) { EnConstruccion("Iniciar ruta", navController) }                              // Taylor

        composable(Rutas.PERFIL) { EnConstruccion("Perfil", navController) }            // compañero

        // ----- Encargado -----
        composable(Rutas.ENC_MI_RECINTO) { EnConstruccion("Mi recinto", navController) }       // compañero
        composable(Rutas.ENC_ESTADO) { EnConstruccion("Cambiar estado", navController) }       // compañero
        composable(Rutas.ENC_HORARIOS) { EnConstruccion("Editar horarios", navController) }    // compañero
        composable(Rutas.ENC_EVENTOS) { EnConstruccion("Eventos del recinto", navController) } // compañero

        composable(
            route = Rutas.ENC_EVENTO_FORM,
            arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
        ) { EnConstruccion("Crear / editar evento", navController) }                            // compañero

        composable(Rutas.ENC_GUIAS) { EnConstruccion("Disponibilidad de guías", navController) } // Taylor
    }
}
