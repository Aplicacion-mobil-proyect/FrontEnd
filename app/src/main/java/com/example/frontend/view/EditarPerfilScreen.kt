package com.example.frontend.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.R
import com.example.frontend.ui.theme.ColoresAvatar
import com.example.frontend.view.components.AvatarUsuario
import com.example.frontend.view.components.TopBarVolver
import com.example.frontend.viewmodel.EditarPerfilViewModel

@Composable
fun EditarPerfilScreen(
    navController: NavController,
    vm: EditarPerfilViewModel = viewModel()
) {
    // Al guardar bien, vuelve al Perfil (que se actualiza solo gracias al Flow)
    LaunchedEffect(vm.guardadoExitoso) {
        if (vm.guardadoExitoso) navController.popBackStack()
    }

    Scaffold(
        topBar = { TopBarVolver(stringResource(R.string.editar_perfil)) { navController.popBackStack() } }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().imePadding()
                .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ---------- Avatar: vista previa + colores ----------
            AvatarUsuario(
                nombre = vm.nombre,
                avatar = vm.avatar,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(stringResource(R.string.color_avatar), style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ColoresAvatar.forEachIndexed { indice, color ->
                    val elegido = indice == vm.avatar
                    Box(
                        Modifier.size(40.dp)
                            .background(color, CircleShape)
                            .border(if (elegido) 3.dp else 0.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            .clickable { vm.cambiarAvatar(indice) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (elegido) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                    }
                }
            }

            // ---------- Datos personales ----------
            Text(
                stringResource(R.string.datos_personales),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Campo(vm.nombre, vm::cambiarNombre, R.string.nombre_completo, vm.errorNombre)
            Campo(vm.apodo, vm::cambiarApodo, R.string.apodo, vm.errorApodo, ayuda = R.string.apodo_ayuda)
            Campo(vm.email, vm::cambiarEmail, R.string.correo, vm.errorEmail, teclado = KeyboardType.Email)

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            // ---------- Contraseña (opcional) ----------
            Text(stringResource(R.string.cambiar_password), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.cambiar_password_ayuda),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Campo(vm.passwordActual, vm::cambiarPasswordActual, R.string.password_actual, vm.errorPasswordActual, esPassword = true)
            Campo(vm.passwordNueva, vm::cambiarPasswordNueva, R.string.password_nueva, vm.errorPasswordNueva, esPassword = true)
            Campo(vm.passwordConfirmar, vm::cambiarPasswordConfirmar, R.string.password_confirmar, vm.errorPasswordConfirmar, esPassword = true)

            vm.errorGeneral?.let {
                Text(stringResource(it), color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { vm.guardar() },
                enabled = !vm.cargando,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(52.dp)
            ) {
                if (vm.cargando) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text(stringResource(R.string.guardar_cambios))
                }
            }
        }
    }
}

/** Campo de texto con etiqueta y error traducidos. */
@Composable
private fun Campo(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: Int,
    error: Int?,
    ayuda: Int? = null,
    teclado: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false
) {
    val textoAbajo = error ?: ayuda   // el error tiene prioridad sobre el texto de ayuda
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(stringResource(etiqueta)) },
        singleLine = true,
        isError = error != null,
        supportingText = if (textoAbajo != null) { { Text(stringResource(textoAbajo)) } } else null,
        visualTransformation = if (esPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esPassword) KeyboardType.Password else teclado),
        modifier = Modifier.fillMaxWidth()
    )
}
