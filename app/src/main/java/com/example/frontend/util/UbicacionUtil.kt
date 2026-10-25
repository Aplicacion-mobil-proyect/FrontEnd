package com.example.frontend.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object UbicacionUtil {
    // Referencia por defecto: Plaza de Armas, centro de Santiago
    const val PLAZA_LAT = -33.4378
    const val PLAZA_LNG = -70.6504

    /** Distancia en km entre dos puntos (fórmula de Haversine). */
    fun distanciaKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2) * sin(dLng / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun formatear(km: Double): String =
        if (km < 1) "${(km * 1000).roundToInt()} m" else String.format(Locale("es", "CL"), "%.1f km", km)

    /** Última ubicación conocida del teléfono. Solo llamar con el permiso ya concedido. */
    @SuppressLint("MissingPermission")
    fun ultimaUbicacion(context: Context): Location? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        return try {
            listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
                .mapNotNull { p -> try { lm.getLastKnownLocation(p) } catch (e: Exception) { null } }
                .maxByOrNull { it.time }
        } catch (e: SecurityException) {
            null
        }
    }
}
