package com.example.frontend

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.database.SeedData
import com.example.frontend.navigation.AppNavigation
import com.example.frontend.ui.theme.FrontEndTheme
import com.example.frontend.util.IdiomaManager
import com.example.frontend.util.TemaManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Se ejecuta antes de onCreate: aplica el idioma guardado a toda la app.
    // Cuando el usuario cambia el idioma, SelectorIdioma llama a recreate() y esto vuelve a correr.
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IdiomaManager.envolver(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Carga los datos ficticios la primera vez que se abre la app
        lifecycleScope.launch {
            SeedData.cargarSiEstaVacia(AppDatabase.getDatabase(applicationContext))
        }

        // Lee la paleta de colores guardada antes de dibujar
        TemaManager.cargar(this)

        setContent {
            FrontEndTheme {
                AppNavigation()
            }
        }
    }
}
