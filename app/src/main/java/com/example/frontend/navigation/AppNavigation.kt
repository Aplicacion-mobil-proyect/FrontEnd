package com.example.frontend.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontend.view.CambiarEstadoScreen
import com.example.frontend.view.CercanosScreen
import com.example.frontend.view.CrearRutaScreen
import com.example.frontend.view.EditarHorariosScreen
import com.example.frontend.view.EventoDetalleScreen
import com.example.frontend.view.EventoFormScreen
import com.example.frontend.view.EventosScreen
import com.example.frontend.view.FavoritosScreen
import com.example.frontend.view.FichaScreen
import com.example.frontend.view.GuiasScreen
import com.example.frontend.view.HorariosEspecialesScreen
import com.example.frontend.view.GestionEventosScreen
import com.example.frontend.view.IniciarRutaScreen
import com.example.frontend.view.HomeScreen
import com.example.frontend.view.MapaScreen
import com.example.frontend.view.MiRecintoScreen
import com.example.frontend.view.MisRutasScreen
import com.example.frontend.view.MisVisitasScreen
import com.example.frontend.view.LoginScreen
import com.example.frontend.view.PerfilScreen
import com.example.frontend.view.PlanificarVisitaScreen
import com.example.frontend.view.QrScreen
import com.example.frontend.view.RegistroScreen
import com.example.frontend.view.SplashScreen
import com.example.frontend.view.components.EnConstruccion

// Duración de la animación entre pantallas, en milisegundos
private const val DURACION_ANIMACION = 350

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Siempre arranca en el Splash: él decide si va a Login, Home o Mi recinto según la sesión guardada
    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        // Animación tipo "pasar página": la pantalla nueva entra desde la derecha
        // y la anterior se desplaza hacia la izquierda. Al volver, al revés.
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION_ANIMACION))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION_ANIMACION))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION_ANIMACION))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION_ANIMACION))
        }
    ) {

        // ----- Acceso -----
        composable(Rutas.SPLASH) { SplashScreen(navController) }
        composable(Rutas.LOGIN) { LoginScreen(navController) }
        composable(Rutas.REGISTRO) { RegistroScreen(navController) }

        // ----- Turista / colegio -----
        composable(Rutas.HOME) { HomeScreen(navController) }
        composable(Rutas.MAPA) { MapaScreen(navController) }
        composable(Rutas.CERCANOS) { CercanosScreen(navController) }
        composable(Rutas.ESCANER_QR) { QrScreen(navController) }                          // Taylor

        composable(
            route = Rutas.FICHA,
            arguments = listOf(navArgument("recintoId") { type = NavType.IntType })
        ) {
            FichaScreen(navController)
        }

        composable(Rutas.EVENTOS) { EventosScreen(navController) }
        composable(
            route = Rutas.EVENTO_DETALLE,
            arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
        ) { EventoDetalleScreen(navController) }

        composable(Rutas.FAVORITOS) { FavoritosScreen(navController) }
        composable(Rutas.MIS_VISITAS) { MisVisitasScreen(navController) }

        composable(
            route = Rutas.PLANIFICAR_VISITA,
            arguments = listOf(navArgument("recintoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recintoId = backStackEntry.arguments?.getInt("recintoId") ?: 0
            PlanificarVisitaScreen(navController, recintoId)
        }

        composable(Rutas.MIS_RUTAS) { MisRutasScreen(navController) }
        composable(Rutas.CREAR_RUTA) { CrearRutaScreen(navController) }
        composable(
            route = Rutas.INICIAR_RUTA,
            arguments = listOf(navArgument("rutaId") { type = NavType.IntType })
        ) { IniciarRutaScreen(navController) }

        composable(Rutas.PERFIL) { PerfilScreen(navController) }

        // ----- Encargado -----
        composable(Rutas.ENC_MI_RECINTO) { MiRecintoScreen(navController) }
        composable(Rutas.ENC_ESTADO) { CambiarEstadoScreen(navController) }
        composable(Rutas.ENC_HORARIOS) { EditarHorariosScreen(navController) }
        composable(Rutas.ENC_EVENTOS) { GestionEventosScreen(navController) }
        composable(
            route = Rutas.ENC_EVENTO_FORM,
            arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
        ) { EventoFormScreen(navController) }

        composable(Rutas.ENC_GUIAS) { GuiasScreen(navController) }
        composable(Rutas.ENC_HORARIOS_ESPECIALES) { HorariosEspecialesScreen(navController) }
    }
}
