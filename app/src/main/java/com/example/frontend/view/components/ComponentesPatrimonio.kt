package com.example.frontend.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.util.etiquetaEstadoRecinto
import com.example.frontend.viewmodel.UiState

// Componentes compartidos por las pantallas de eventos, favoritos, visitas y encargado.
// Llevan el prefijo "Pat" para no chocar con los que ya existen en esta carpeta.

/** Pantalla con barra superior (flecha atrás opcional), Snackbar y botón flotante opcional. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatScaffold(
    titulo: String,
    snackbarHostState: SnackbarHostState,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {},
    fab: @Composable () -> Unit = {},
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (onVolver != null) IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = acciones
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = fab,
        content = contenido
    )
}

/** Muestra el mensaje del ViewModel en un Snackbar y luego lo limpia. */
@Composable
fun PatMensajes(mensaje: String?, host: SnackbarHostState, alMostrar: () -> Unit) {
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            host.showSnackbar(mensaje)
            alMostrar()
        }
    }
}

@Composable
fun PatMensajeCentral(texto: String, modifier: Modifier = Modifier, esError: Boolean = false) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Dibuja Loading / Error / Success según el UiState. */
@Composable
fun <T> PatUiContent(state: UiState<T>, modifier: Modifier = Modifier, contenido: @Composable (T) -> Unit) {
    when (state) {
        is UiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is UiState.Error -> PatMensajeCentral(state.mensaje, modifier, esError = true)
        is UiState.Success -> Box(modifier.fillMaxSize()) { contenido(state.data) }
    }
}

@Composable
fun PatEstadoChip(estado: String, modifier: Modifier = Modifier) {
    val (fondo, texto) = when (estado) {
        EstadosRecinto.ABIERTO -> Color(0xFFDDF3E4) to Color(0xFF1B5E20)
        EstadosRecinto.CERRADO -> Color(0xFFFDE0E0) to Color(0xFFB71C1C)
        EstadosRecinto.EN_CELEBRACION -> Color(0xFFFFF1CC) to Color(0xFF7A4F00)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = fondo, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            etiquetaEstadoRecinto(estado),
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PatConfirmDialog(
    titulo: String,
    texto: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(texto) },
        confirmButton = { TextButton(onClick = onConfirmar) { Text(textoConfirmar) } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
fun PatCampo(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    lineas: Int = 1,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        placeholder = if (placeholder != null) { { Text(placeholder) } } else null,
        isError = error != null,
        supportingText = if (error != null) { { Text(error) } } else null,
        singleLine = lineas == 1,
        minLines = lineas,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = modifier.fillMaxWidth()
    )
}
