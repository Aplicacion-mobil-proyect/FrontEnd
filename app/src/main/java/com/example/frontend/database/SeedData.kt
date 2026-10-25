package com.example.frontend.database

import com.example.frontend.model.DisponibilidadGuia
import com.example.frontend.model.EstadosRecinto
import com.example.frontend.model.Evento
import com.example.frontend.model.Guia
import com.example.frontend.model.Horario
import com.example.frontend.model.Recinto
import com.example.frontend.model.Roles
import com.example.frontend.model.Usuario
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/*
 * Datos ficticios que se cargan la primera vez que se abre la app.
 * Los ids de recintos están fijos (1, 2, 3...) para que horarios, eventos y encargados
 * puedan apuntar a su recinto. Si agregan recintos, sigan la numeración.
 *
 * TODO (compañero): completar con al menos 10 recintos reales de Santiago,
 * con sus horarios, eventos y disponibilidad de guías.
 */
object SeedData {

    // Evita que los datos se carguen dos veces si se llama al mismo tiempo desde dos lugares
    private val mutex = Mutex()

    suspend fun cargarSiEstaVacia(db: AppDatabase) {
        mutex.withLock {
            if (db.recintoDao().contar() > 0) return   // ya hay datos

            db.recintoDao().insertarTodos(recintos)
            db.usuarioDao().insertarTodos(usuarios)
            db.horarioDao().insertarTodos(horarios)
            db.eventoDao().insertarTodos(eventos)
            db.guiaDao().insertarTodos(guias)
            db.guiaDao().insertarDisponibilidades(disponibilidad)
        }
    }

    // Contraseña de todos los usuarios de prueba: 1234
    private val usuarios = listOf(
        Usuario(id = 1, nombre = "Turista Demo", email = "turista@demo.cl", password = "1234", rol = Roles.TURISTA),
        Usuario(id = 2, nombre = "Colegio Demo", email = "colegio@demo.cl", password = "1234", rol = Roles.COLEGIO),
        Usuario(id = 3, nombre = "Encargado Catedral", email = "encargado@demo.cl", password = "1234",
            rol = Roles.ENCARGADO, recintoId = 1)
    )

    private val recintos = listOf(
        Recinto(
            id = 1, nombre = "Catedral Metropolitana de Santiago", tipo = "Catedral",
            direccion = "Plaza de Armas s/n", comuna = "Santiago",
            latitud = -33.4375, longitud = -70.6515,
            descripcion = "Principal templo católico de Chile. Su construcción actual comenzó en 1748 " +
                "y combina estilos neoclásico y barroco. Es la sede del Arzobispado de Santiago.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 45
        ),
        Recinto(
            id = 2, nombre = "Basílica de la Merced", tipo = "Basílica",
            direccion = "Mac Iver 341", comuna = "Santiago",
            latitud = -33.4369, longitud = -70.6482,
            descripcion = "Templo de la orden mercedaria reconstruido en el siglo XVIII. " +
                "Alberga un museo con arte colonial y piezas de Rapa Nui.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 30
        ),
        Recinto(
            id = 3, nombre = "Iglesia de San Francisco", tipo = "Iglesia",
            direccion = "Av. Libertador Bernardo O'Higgins 834", comuna = "Santiago",
            latitud = -33.4446, longitud = -70.6480,
            descripcion = "El edificio más antiguo de Santiago (1586). Ha resistido numerosos terremotos " +
                "y conserva la imagen de la Virgen del Socorro.",
            estado = EstadosRecinto.EN_CELEBRACION, duracionVisita = 40
        )
    )

    private val horarios = listOf(
        Horario(recintoId = 1, dia = 1, apertura = "09:00", cierre = "19:00"),
        Horario(recintoId = 1, dia = 2, apertura = "09:00", cierre = "19:00"),
        Horario(recintoId = 1, dia = 3, apertura = "09:00", cierre = "19:00"),
        Horario(recintoId = 1, dia = 4, apertura = "09:00", cierre = "19:00"),
        Horario(recintoId = 1, dia = 5, apertura = "09:00", cierre = "19:00"),
        Horario(recintoId = 1, dia = 6, apertura = "09:00", cierre = "19:00"),
        Horario(recintoId = 1, dia = 7, apertura = "09:00", cierre = "13:00"),
        Horario(recintoId = 2, dia = 1, apertura = "10:00", cierre = "18:00"),
        Horario(recintoId = 2, dia = 3, apertura = "10:00", cierre = "18:00"),
        Horario(recintoId = 2, dia = 5, apertura = "10:00", cierre = "18:00"),
        Horario(recintoId = 3, dia = 1, apertura = "08:00", cierre = "20:00"),
        Horario(recintoId = 3, dia = 4, apertura = "20:00", cierre = "23:00",
            especial = true, descripcion = "Vigilia de Semana Santa")
    )

    private val eventos = listOf(
        Evento(recintoId = 1, titulo = "Concierto de música sacra",
            descripcion = "Coro y órgano interpretan obras de compositores coloniales.",
            tipo = "Música sacra", fecha = "2026-11-14", hora = "19:00"),
        Evento(recintoId = 2, titulo = "Exposición de arte colonial",
            descripcion = "Muestra de pinturas y tallas de los siglos XVII y XVIII.",
            tipo = "Exposición", fecha = "2026-11-20", hora = "10:00"),
        Evento(recintoId = 3, titulo = "Fiesta de San Francisco de Asís",
            descripcion = "Misa solemne y bendición de animales en el atrio.",
            tipo = "Litúrgico", fecha = "2026-12-04", hora = "11:00")
    )

    private val guias = listOf(
        Guia(id = 1, nombre = "María José Rojas", idiomas = "Español, Inglés"),
        Guia(id = 2, nombre = "Pedro Valenzuela", idiomas = "Español, Portugués")
    )

    private val disponibilidad = listOf(
        DisponibilidadGuia(guiaId = 1, recintoId = 1, dia = 1, inicio = "10:00", fin = "14:00"),
        DisponibilidadGuia(guiaId = 1, recintoId = 1, dia = 3, inicio = "10:00", fin = "14:00"),
        DisponibilidadGuia(guiaId = 1, recintoId = 1, dia = 5, inicio = "10:00", fin = "14:00"),
        DisponibilidadGuia(guiaId = 2, recintoId = 2, dia = 2, inicio = "14:00", fin = "18:00"),
        DisponibilidadGuia(guiaId = 2, recintoId = 3, dia = 4, inicio = "14:00", fin = "18:00")
    )
}
