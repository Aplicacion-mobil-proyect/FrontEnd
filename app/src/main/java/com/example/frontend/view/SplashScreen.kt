package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend.model.Roles
import com.example.frontend.navigation.Rutas
import com.example.frontend.session.Sesion
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1500)
        // Sin sesión -> Login. Con sesión -> pantalla de inicio según el rol.
        val usuario = Sesion.obtener(context)
        val destino = when {
            usuario == null -> Rutas.LOGIN
            usuario.rol == Roles.ENCARGADO -> Rutas.ENC_MI_RECINTO
            else -> Rutas.HOME
        }
        navController.navigate(destino) { popUpTo(Rutas.SPLASH) { inclusive = true } }
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(72.dp))
            Text("Red Patrimonial", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
            Text("de Santiago", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
