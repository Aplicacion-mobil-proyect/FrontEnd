package com.example.frontend.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend.model.Roles
import com.example.frontend.navigation.Rutas
import com.example.frontend.session.Sesion

// Colores propios de la Home (tonos piedra cálidos, combinan con el burdeo del tema)
private val FondoHome = Color(0xFFF3EFEA)
private val FondoDestacado = Color(0xFFDCD6D0)
private val LogoTexto = Color(0xFFF6D9DD)

/*
 * Pantalla principal del turista / colegio.
 * Arriba: encabezado con logo, buscar y perfil.
 * Luego: una tarjeta grande (mapa), tarjetas en dos columnas y tarjetas anchas.
 * Cada tarjeta navega a una pantalla distinta con navController.navigate(...).
 */
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val usuario = Sesion.obtener(context)
    val esEncargado = usuario?.rol == Roles.ENCARGADO

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoHome)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ---------- Encabezado ----------
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = LogoTexto)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text("Red Patrimonial", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (usuario != null) "Hola, ${usuario.nombre.substringBefore(' ')}" else "Arzobispado de Santiago",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { navController.navigate(Rutas.CERCANOS) }) {
                Icon(Icons.Outlined.Search, contentDescription = "Buscar recintos")
            }
            IconButton(onClick = { navController.navigate(Rutas.PERFIL) }) {
                Icon(Icons.Outlined.Person, contentDescription = "Mi perfil")
            }
        }

        // Acceso directo para el encargado
        if (esEncargado) {
            TarjetaAncha(
                icono = Icons.Outlined.Store,
                titulo = "Mi recinto",
                subtitulo = "Estado, horarios, eventos y guías",
                onClick = { navController.navigate(Rutas.ENC_MI_RECINTO) }
            )
        }

        // ---------- Tarjeta destacada: mapa ----------
        Card(
            onClick = { navController.navigate(Rutas.MAPA) },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FondoDestacado),
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Outlined.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text("Explora el patrimonio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Templos, edificios históricos y rutas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ---------- Tarjetas en dos columnas ----------
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaChica(
                icono = Icons.Outlined.AccountBalance,
                titulo = "Templos",
                subtitulo = "Historia y arquitectura",
                onClick = { navController.navigate(Rutas.CERCANOS) },
                modifier = Modifier.weight(1f)
            )
            TarjetaChica(
                icono = Icons.Outlined.Directions,
                titulo = "Rutas",
                subtitulo = "Recorridos patrimoniales",
                onClick = { navController.navigate(Rutas.MIS_RUTAS) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaChica(
                icono = Icons.Outlined.Event,
                titulo = "Eventos",
                subtitulo = "Música sacra y exposiciones",
                onClick = { navController.navigate(Rutas.EVENTOS) },
                modifier = Modifier.weight(1f)
            )
            TarjetaChica(
                icono = Icons.Outlined.QrCodeScanner,
                titulo = "Escanear QR",
                subtitulo = "En la entrada del templo",
                onClick = { navController.navigate(Rutas.ESCANER_QR) },
                modifier = Modifier.weight(1f)
            )
        }

        // ---------- Tarjetas anchas ----------
        TarjetaAncha(
            icono = Icons.Outlined.FavoriteBorder,
            titulo = "Mis favoritos",
            subtitulo = "Los recintos que guardaste",
            onClick = { navController.navigate(Rutas.FAVORITOS) }
        )
        TarjetaAncha(
            icono = Icons.Outlined.EventAvailable,
            titulo = "Mis visitas",
            subtitulo = "Visitas agendadas y guías asignados",
            onClick = { navController.navigate(Rutas.MIS_VISITAS) }
        )

        Text(
            "Arzobispado de Santiago · Área de Cultura",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

// Tarjeta cuadrada: ícono arriba, título y subtítulo abajo
@Composable
private fun TarjetaChica(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.height(130.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Tarjeta ancha: ícono a la izquierda, textos al centro y flecha a la derecha
@Composable
private fun TarjetaAncha(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
