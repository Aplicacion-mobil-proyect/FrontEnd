package com.example.frontend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.frontend.R
import com.example.frontend.view.components.BotonIdioma
import androidx.compose.foundation.layout.Box
import com.example.frontend.model.Roles
import com.example.frontend.navigation.Rutas
import com.example.frontend.session.Sesion
import com.example.frontend.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    var verPassword by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Cuando el login es exitoso: el encargado va a "Mi recinto" y los demás a Home.
    // popUpTo borra el login del historial para que "atrás" no vuelva a él.
    LaunchedEffect(loginViewModel.loginExitoso) {
        if (loginViewModel.loginExitoso) {
            val destino = if (Sesion.obtener(context)?.rol == Roles.ENCARGADO) Rutas.ENC_MI_RECINTO else Rutas.HOME
            navController.navigate(destino) {
                popUpTo(Rutas.LOGIN) { inclusive = true }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp)
        ) {
            Icon(
                Icons.Filled.AccountBalance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Red Patrimonial",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = stringResource(R.string.pie_arzobispado),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = loginViewModel.email,
                onValueChange = { loginViewModel.cambiarEmail(it) },
                label = { Text(stringResource(R.string.correo)) },
                singleLine = true,
                isError = loginViewModel.error != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.cambiarPassword(it) },
                label = { Text(stringResource(R.string.contrasena)) },
                singleLine = true,
                isError = loginViewModel.error != null,
                supportingText = {
                    loginViewModel.error?.let { Text(stringResource(it)) }
                },
                visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(
                            imageVector = if (verPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = stringResource(R.string.mostrar_password)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            Button(
                onClick = { loginViewModel.login() },
                enabled = !loginViewModel.cargando,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth().height(52.dp)
            ) {
                if (loginViewModel.cargando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(stringResource(R.string.ingresar))
                }
            }

            TextButton(
                onClick = { navController.navigate(Rutas.REGISTRO) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.sin_cuenta))
            }

            Text(
                text = stringResource(R.string.usuarios_prueba),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp)
            )
        }

        // Botón de idioma arriba a la derecha; va después de la Column para quedar encima
        Box(Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp)) { BotonIdioma() }
    }
}
