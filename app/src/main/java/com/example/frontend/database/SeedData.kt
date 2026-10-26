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
 *
 * Incluye recintos del centro y de otras comunas (Recoleta, Providencia, Quinta Normal,
 * Las Condes y Maipú), porque el problema del caso es que las visitas se concentran
 * cerca de la Plaza de Armas. Las coordenadas son aproximadas.
 *
 * Los ids de recintos están fijos (1, 2, 3...) para que horarios, eventos, guías y el encargado
 * puedan apuntar a su recinto. Si se modifican estos datos, hay que subir la "version"
 * en AppDatabase para que la base se vuelva a crear.
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

    // ------------------------------------------------------------------
    // Usuarios de prueba (contraseña de todos: 1234)
    // ------------------------------------------------------------------
    private val usuarios = listOf(
        Usuario(id = 1, nombre = "Turista Demo", email = "turista@demo.cl", password = "1234", rol = Roles.TURISTA),
        Usuario(id = 2, nombre = "Colegio Demo", email = "colegio@demo.cl", password = "1234", rol = Roles.COLEGIO),
        Usuario(id = 3, nombre = "Encargado Catedral", email = "encargado@demo.cl", password = "1234",
            rol = Roles.ENCARGADO, recintoId = 1),
        Usuario(id = 4, nombre = "Encargado Votivo de Maipú", email = "maipu@demo.cl", password = "1234",
            rol = Roles.ENCARGADO, recintoId = 9)
    )

    // ------------------------------------------------------------------
    // Recintos patrimoniales
    // ------------------------------------------------------------------
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
        ),
        Recinto(
            id = 4, nombre = "Iglesia de Santo Domingo", tipo = "Iglesia",
            direccion = "Santo Domingo 961", comuna = "Santiago",
            latitud = -33.4351, longitud = -70.6497,
            descripcion = "Templo dominico de piedra construido entre 1747 y 1808, con fachada de estilo " +
                "barroco y neoclásico. Es Monumento Histórico desde 1951.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 30
        ),
        Recinto(
            id = 5, nombre = "Basílica de los Sacramentinos", tipo = "Basílica",
            direccion = "Arturo Prat 471", comuna = "Santiago",
            latitud = -33.4560, longitud = -70.6478,
            descripcion = "Inspirada en la basílica del Sagrado Corazón de Montmartre en París. " +
                "Fue consagrada en 1931 y destaca por su cripta y su gran cúpula.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 35
        ),
        Recinto(
            id = 6, nombre = "Museo de la Recoleta Dominica", tipo = "Museo",
            direccion = "Av. Recoleta 683", comuna = "Recoleta",
            latitud = -33.4278, longitud = -70.6435,
            descripcion = "Antiguo convento dominico del siglo XIX que hoy reúne colecciones de artes " +
                "decorativas y una biblioteca patrimonial con libros de la época colonial.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 60
        ),
        Recinto(
            id = 7, nombre = "Santuario Inmaculada Concepción", tipo = "Santuario",
            direccion = "Cumbre del Cerro San Cristóbal", comuna = "Recoleta",
            latitud = -33.4253, longitud = -70.6330,
            descripcion = "En la cumbre del cerro San Cristóbal se encuentra la imagen de la Virgen, " +
                "de 14 metros de altura, inaugurada en 1908, junto a un santuario al aire libre.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 50
        ),
        Recinto(
            id = 8, nombre = "Basílica de Lourdes", tipo = "Basílica",
            direccion = "Av. Libertador Bernardo O'Higgins 4011", comuna = "Quinta Normal",
            latitud = -33.4320, longitud = -70.6880,
            descripcion = "Basílica de hormigón armado consagrada en 1958, con una gruta que replica la de " +
                "Lourdes en Francia. Es un importante lugar de peregrinación del sector poniente.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 40
        ),
        Recinto(
            id = 9, nombre = "Templo Votivo de Maipú", tipo = "Santuario",
            direccion = "Av. 5 de Abril 260", comuna = "Maipú",
            latitud = -33.5100, longitud = -70.7570,
            descripcion = "Santuario Nacional construido en agradecimiento a la Virgen del Carmen por la " +
                "Independencia de Chile. Junto a las ruinas del templo original de 1818.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 60
        ),
        Recinto(
            id = 10, nombre = "Museo del Carmen de Maipú", tipo = "Museo",
            direccion = "Av. 5 de Abril 260", comuna = "Maipú",
            latitud = -33.5105, longitud = -70.7578,
            descripcion = "Museo dedicado a la historia de Chile y al patrimonio religioso, con carruajes, " +
                "vestuario y objetos de la época colonial y republicana.",
            estado = EstadosRecinto.CERRADO, duracionVisita = 60
        ),
        Recinto(
            id = 11, nombre = "Iglesia de los Dominicos", tipo = "Iglesia",
            direccion = "Av. Apoquindo 9085", comuna = "Las Condes",
            latitud = -33.4085, longitud = -70.5450,
            descripcion = "Iglesia de San Vicente Ferrer, construida a fines del siglo XVIII. Sus torres y " +
                "muros blancos son un hito del sector oriente, junto al Pueblito de Los Dominicos.",
            estado = EstadosRecinto.ABIERTO, duracionVisita = 30
        ),
        Recinto(
            id = 12, nombre = "Parroquia de la Divina Providencia", tipo = "Iglesia",
            direccion = "Av. Providencia 1200", comuna = "Providencia",
            latitud = -33.4290, longitud = -70.6170,
            descripcion = "Templo de fines del siglo XIX que dio nombre a la comuna de Providencia. " +
                "Destaca por sus dos torres y su interior de estilo neogótico.",
            estado = EstadosRecinto.EN_CELEBRACION, duracionVisita = 30
        )
    )

    // ------------------------------------------------------------------
    // Horarios (dia: 1 = lunes ... 7 = domingo)
    // ------------------------------------------------------------------

    // Crea el mismo horario para varios días
    private fun semana(recintoId: Int, dias: IntRange, apertura: String, cierre: String) =
        dias.map { Horario(recintoId = recintoId, dia = it, apertura = apertura, cierre = cierre) }

    private val horarios =
        semana(1, 1..6, "09:00", "19:00") + semana(1, 7..7, "09:00", "13:00") +
        semana(2, 1..5, "10:00", "18:00") +
        semana(3, 1..7, "08:00", "20:00") +
        semana(4, 1..6, "10:00", "18:00") +
        semana(5, 1..7, "09:00", "19:00") +
        semana(6, 2..6, "10:00", "17:00") +
        semana(7, 1..7, "08:30", "19:00") +
        semana(8, 1..7, "08:00", "20:00") +
        semana(9, 1..7, "09:00", "19:00") +
        semana(10, 2..7, "10:00", "18:00") +
        semana(11, 1..7, "10:00", "19:00") +
        semana(12, 1..6, "09:00", "13:00") +
        listOf(
            Horario(recintoId = 1, dia = 4, apertura = "20:00", cierre = "23:00",
                especial = true, descripcion = "Vigilia de Semana Santa"),
            Horario(recintoId = 3, dia = 4, apertura = "20:00", cierre = "23:00",
                especial = true, descripcion = "Vigilia de Semana Santa"),
            Horario(recintoId = 9, dia = 7, apertura = "19:00", cierre = "22:00",
                especial = true, descripcion = "Fiesta de la Virgen del Carmen")
        )

    // ------------------------------------------------------------------
    // Eventos culturales
    // ------------------------------------------------------------------
    private val eventos = listOf(
        Evento(recintoId = 1, titulo = "Concierto de música sacra",
            descripcion = "Coro y órgano interpretan obras de compositores coloniales.",
            tipo = "Música sacra", fecha = "2026-11-14", hora = "19:00"),
        Evento(recintoId = 1, titulo = "Misa de Navidad",
            descripcion = "Celebración solemne presidida por el Arzobispo de Santiago.",
            tipo = "Litúrgico", fecha = "2026-12-24", hora = "23:00"),
        Evento(recintoId = 2, titulo = "Exposición de arte colonial",
            descripcion = "Muestra de pinturas y tallas de los siglos XVII y XVIII.",
            tipo = "Exposición", fecha = "2026-11-20", hora = "10:00"),
        Evento(recintoId = 3, titulo = "Fiesta de San Francisco de Asís",
            descripcion = "Misa solemne y bendición de animales en el atrio.",
            tipo = "Litúrgico", fecha = "2026-12-04", hora = "11:00"),
        Evento(recintoId = 5, titulo = "Recital de órgano",
            descripcion = "Recorrido por la música para órgano desde el barroco hasta el siglo XX.",
            tipo = "Música sacra", fecha = "2026-11-28", hora = "19:30"),
        Evento(recintoId = 6, titulo = "Libros que cruzaron el océano",
            descripcion = "Exposición de libros antiguos de la biblioteca patrimonial dominica.",
            tipo = "Exposición", fecha = "2026-11-08", hora = "11:00"),
        Evento(recintoId = 7, titulo = "Fiesta de la Inmaculada Concepción",
            descripcion = "Peregrinación y misa al aire libre en la cumbre del cerro San Cristóbal.",
            tipo = "Litúrgico", fecha = "2026-12-08", hora = "10:00"),
        Evento(recintoId = 8, titulo = "Coro de niños de Quinta Normal",
            descripcion = "Concierto de villancicos con coros de colegios de la comuna.",
            tipo = "Música sacra", fecha = "2026-12-12", hora = "18:00"),
        Evento(recintoId = 9, titulo = "Historia de la Independencia en el Votivo",
            descripcion = "Exposición sobre la batalla de Maipú y la construcción del templo.",
            tipo = "Exposición", fecha = "2026-11-15", hora = "12:00"),
        Evento(recintoId = 11, titulo = "Noche de música sacra en Los Dominicos",
            descripcion = "Conjunto de cámara interpreta obras de Bach y Vivaldi.",
            tipo = "Música sacra", fecha = "2026-11-21", hora = "20:00")
    )

    // ------------------------------------------------------------------
    // Guías voluntarios y su disponibilidad
    // ------------------------------------------------------------------
    private val guias = listOf(
        Guia(id = 1, nombre = "María José Rojas", idiomas = "Español, Inglés"),
        Guia(id = 2, nombre = "Pedro Valenzuela", idiomas = "Español, Portugués"),
        Guia(id = 3, nombre = "Camila Fuentes", idiomas = "Español, Francés"),
        Guia(id = 4, nombre = "Jorge Muñoz", idiomas = "Español")
    )

    // Bloques de disponibilidad para varios días
    private fun bloques(guiaId: Int, recintoId: Int, dias: IntRange, inicio: String, fin: String) =
        dias.map { DisponibilidadGuia(guiaId = guiaId, recintoId = recintoId, dia = it, inicio = inicio, fin = fin) }

    private val disponibilidad =
        bloques(1, 1, 1..5, "10:00", "14:00") +      // María José: Catedral, lunes a viernes en la mañana
        bloques(2, 2, 1..5, "14:00", "18:00") +      // Pedro: La Merced, lunes a viernes en la tarde
        bloques(2, 3, 6..7, "10:00", "14:00") +      // Pedro: San Francisco, fines de semana
        bloques(3, 6, 2..6, "10:00", "16:00") +      // Camila: Recoleta Dominica
        bloques(3, 7, 6..7, "10:00", "15:00") +      // Camila: Cerro San Cristóbal, fines de semana
        bloques(4, 9, 1..7, "10:00", "17:00")        // Jorge: Votivo de Maipú, todos los días
}
