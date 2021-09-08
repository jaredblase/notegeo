package com.mobdeve.s15.group5.notegeo

import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

data class LandmarkDataObject(val id: String, val latLong: LatLng)

internal object GeofencingConstants {

    /**
     * Used to set an expiration time for a geofence. After this amount of time, Location services
     * stops tracking the geofence. For this sample, geofences expire after one hour.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)
    const val GEOFENCE_RADIUS_IN_METERS = 100f

    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}