package com.example.frontend.view

import com.example.frontend.view.components.tipoEventoTexto
import com.example.frontend.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.util.TIPOS_EVENTO
import com.example.frontend.view.components.PatCampo
import com.example.frontend.view.components.PatMensajeCentral
import com.example.frontend.view.components.PatMensajes
import com.example.frontend.view.components.PatScaffold
import com.example.frontend.viewmodel.EventoFormViewModel

@Composable
fun EventoFormScreen(navController: NavController, vm: EventoFormViewModel = viewModel()) {
    val snack = remember { SnackbarHostState() }
    PatMensajes(vm.mensaje, snack, vm::mensajeMostrado)

    LaunchedEffect(vm.guardado) {
        if (vm.guardado) navController.popBackStack()
    }

    PatScaffold(
        titulo = if (vm.esEdicion) stringResource(R.string.editar_evento) else stringResource(R.string.nuevo_evento),
        snackbarHostState = snack,
        onVolver = { navController.popBackStack() }
    ) { padding ->
        when {
            vm.cargando -> PatMensajeCentral(stringResource(R.string.cargando), Modifier.padding(padding))
            vm.errorCarga != null -> PatMensajeCentral(vm.errorCarga!!, Modifier.padding(padding), esError = true)
            else -> Column(
                Modifier.padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PatCampo(vm.titulo, vm::cambiarTitulo, stringResource(R.string.titulo), vm.errorTitulo)
                PatCampo(vm.descripcion, vm::cambiarDescripcion, stringResource(R.string.descripcion), vm.errorDescripcion, lineas = 4)

                Text(stringResource(R.string.tipo_evento), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 4.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TIPOS_EVENTO.forEach { t ->
                        FilterChip(selected = vm.tipo == t, onClick = { vm.tipo = t }, label = { Text(tipoEventoTexto(t)) })
                    }
                }

                PatCampo(vm.fecha, vm::cambiarFecha, stringResource(R.string.fecha), vm.errorFecha, teclado = KeyboardType.Number, placeholder = stringResource(R.string.formato_fecha))
                PatCampo(vm.hora, vm::cambiarHora, stringResource(R.string.hora), vm.errorHora, placeholder = "HH:mm")

                Button(
                    onClick = { vm.guardar() },
                    enabled = !vm.guardando,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    if (vm.guardando) CircularProgressIndicator(Modifier.padding(2.dp))
                    else Text(if (vm.esEdicion) stringResource(R.string.guardar_cambios) else stringResource(R.string.crear_evento))
                }
            }
        }
    }
}
