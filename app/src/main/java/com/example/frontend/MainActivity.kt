package com.example.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.frontend.database.AppDatabase
import com.example.frontend.database.SeedData
import com.example.frontend.navigation.AppNavigation
import com.example.frontend.ui.theme.FrontEndTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Carga los datos ficticios la primera vez que se abre la app
        lifecycleScope.launch {
            SeedData.cargarSiEstaVacia(AppDatabase.getDatabase(applicationContext))
        }

        setContent {
            FrontEndTheme {
                AppNavigation()
            }
        }
    }
}
