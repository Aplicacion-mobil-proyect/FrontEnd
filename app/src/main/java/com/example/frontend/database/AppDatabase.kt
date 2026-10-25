package com.example.frontend.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.frontend.dao.EventoDao
import com.example.frontend.dao.FavoritoDao
import com.example.frontend.dao.GuiaDao
import com.example.frontend.dao.HorarioDao
import com.example.frontend.dao.RecintoDao
import com.example.frontend.dao.RutaDao
import com.example.frontend.dao.UsuarioDao
import com.example.frontend.dao.VisitaDao
import com.example.frontend.model.DisponibilidadGuia
import com.example.frontend.model.Evento
import com.example.frontend.model.Favorito
import com.example.frontend.model.Guia
import com.example.frontend.model.Horario
import com.example.frontend.model.Recinto
import com.example.frontend.model.Ruta
import com.example.frontend.model.RutaParada
import com.example.frontend.model.Usuario
import com.example.frontend.model.Visita

@Database(
    entities = [
        Usuario::class, Recinto::class, Horario::class, Evento::class, Favorito::class,
        Guia::class, DisponibilidadGuia::class, Visita::class, Ruta::class, RutaParada::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun recintoDao(): RecintoDao
    abstract fun horarioDao(): HorarioDao
    abstract fun eventoDao(): EventoDao
    abstract fun favoritoDao(): FavoritoDao
    abstract fun guiaDao(): GuiaDao
    abstract fun visitaDao(): VisitaDao
    abstract fun rutaDao(): RutaDao

    companion object {
        // Una sola instancia de la base de datos para toda la app (patrón Singleton)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "red_patrimonial_db"
                )
                    // Si cambiamos una entidad, borra la BD y la vuelve a crear (sin migraciones)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
