package com.example.frontend.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Rutas
import com.example.frontend.view.components.TopBarVolver
import com.example.frontend.viewmodel.QrViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/*
 * Lector de QR con Google Code Scanner (Google Play Services).
 * Google muestra su propia pantalla de cámara, por eso no hay que pedir permiso de cámara.
 * Cada recinto tiene en su entrada un QR con el texto "recinto:<id>".
 */
@Composable
fun QrScreen(
    navController: NavController,
    qrViewModel: QrViewModel = viewModel()
) {
    val context = LocalContext.current

    val contenidoQR by qrViewModel.contenidoQR.collectAsStateWithLifecycle()
    val recintoId by qrViewModel.recintoId.collectAsStateWithLifecycle()

    // Solo leer códigos QR (no códigos de barra de productos)
    val opciones = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    val scanner = remember(context, opciones) {
        GmsBarcodeScanning.getClient(context, opciones)
    }

    Scaffold(
        topBar = { TopBarVolver(titulo = "Escanear QR", onVolver = { navController.popBackStack() }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.QrCodeScanner,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Escanea el código QR que está en la entrada del templo para ver su ficha patrimonial",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            val contenido = barcode.rawValue.orEmpty()
                            qrViewModel.guardarQR(contenido)
                        }
                        .addOnCanceledListener {
                            // El usuario cerró el escáner sin leer nada
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Escanear QR")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resultado del escaneo
            if (contenidoQR.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contenido del QR:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(contenidoQR)

                        if (recintoId == null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Este código no pertenece a un recinto de la Red Patrimonial",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Si el QR es de un recinto, permite abrir su ficha
                if (recintoId != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { navController.navigate(Rutas.ficha(recintoId!!)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver ficha del recinto")
                    }
                }
            }
        }
    }
}
