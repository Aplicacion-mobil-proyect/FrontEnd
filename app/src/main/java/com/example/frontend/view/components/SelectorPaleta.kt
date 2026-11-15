package com.example.frontend.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import com.example.frontend.ui.theme.Paleta
import com.example.frontend.util.TemaManager

/** Fila "Paleta de colores" del Perfil. Abre un diálogo con las paletas disponibles. */
@Composable
fun SelectorPaletaFila(modifier: Modifier = Modifier) {
    var abrir by remember { mutableStateOf(false) }
    val actual = TemaManager.paletaActual

    Row(
        modifier.fillMaxWidth().clickable { abrir = true }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(stringResource(R.string.paleta_colores), Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
        Muestras(actual)
    }
    if (abrir) DialogoPaleta(actual) { abrir = false }
}

/** Tres círculos con los colores principales de la paleta. */
@Composable
private fun Muestras(paleta: Paleta) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(paleta.primario, paleta.secundario, paleta.primarioContenedor).forEach { color ->
            Box(Modifier.size(18.dp).background(color, CircleShape))
        }
    }
}

@Composable
private fun DialogoPaleta(actual: Paleta, onCerrar: () -> Unit) {
    val contexto = LocalContext.current
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(stringResource(R.string.paleta_colores)) },
        text = {
            Column(Modifier.selectableGroup()) {
                Paleta.values().forEach { paleta ->
                    Row(
                        Modifier.fillMaxWidth()
                            .selectable(
                                selected = paleta == actual,
                                // Cambia el estado: la app se redibuja al tiro con los colores nuevos
                                onClick = { TemaManager.cambiar(contexto, paleta); onCerrar() },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = paleta == actual, onClick = null)
                        Text(stringResource(paleta.nombre), Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
                        Muestras(paleta)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCerrar) { Text(stringResource(R.string.cancelar)) } }
    )
}
