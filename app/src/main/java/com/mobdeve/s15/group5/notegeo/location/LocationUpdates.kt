package com.mobdeve.s15.group5.notegeo.location

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.*

class LocationUpdates : LifecycleService() {
    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 0
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // get location update
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        return super.onStartCommand(intent, flags, startId)
    }
}