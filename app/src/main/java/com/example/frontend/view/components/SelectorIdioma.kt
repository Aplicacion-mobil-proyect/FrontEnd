package com.example.frontend.view.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.frontend.R
import com.example.frontend.util.Idioma
import com.example.frontend.util.IdiomaManager

private fun Context.buscarActividad(): Activity? {
    var c: Context = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

/** Guarda el idioma y recrea la pantalla para que todos los textos cambien. */
private fun cambiarIdioma(context: Context, idioma: Idioma) {
    if (idioma == IdiomaManager.obtener(context)) return
    IdiomaManager.guardar(context, idioma)
    context.buscarActividad()?.recreate()
}

/** Fila "Idioma: Español" que abre la lista de idiomas. Pensada para el Perfil. */
@Composable
fun SelectorIdiomaFila(modifier: Modifier = Modifier) {
    var abrir by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    val actual = remember { IdiomaManager.obtener(contexto) }

    Row(
        modifier.fillMaxWidth().clickable { abrir = true }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(stringResource(R.string.idioma), Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
        Text(actual.nombre, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
    if (abrir) DialogoIdioma(actual) { abrir = false }
}

/** Botón con el ícono de idioma, para barras superiores (por ejemplo, en el Login). */
@Composable
fun BotonIdioma() {
    var abrir by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    val actual = remember { IdiomaManager.obtener(contexto) }
    IconButton(onClick = { abrir = true }) {
        Icon(Icons.Filled.Language, contentDescription = stringResource(R.string.idioma))
    }
    if (abrir) DialogoIdioma(actual) { abrir = false }
}

@Composable
private fun DialogoIdioma(actual: Idioma, onCerrar: () -> Unit) {
    val contexto = LocalContext.current
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(stringResource(R.string.idioma)) },
        text = {
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Idioma.values().forEach { idioma ->
                    Row(
                        Modifier.fillMaxWidth()
                            .selectable(
                                selected = idioma == actual,
                                onClick = { onCerrar(); cambiarIdioma(contexto, idioma) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = idioma == actual, onClick = null)
                        Text(idioma.nombre, Modifier.padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCerrar) { Text(stringResource(R.string.cancelar)) } }
    )
}
