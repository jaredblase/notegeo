package com.mobdeve.s15.group5.notegeo.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.mobdeve.s15.group5.notegeo.alarms.NotificationUtils

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //Toast.makeText(context, "Geofence Triggered", Toast.LENGTH_LONG).show()
        //Log.d("Maps", "onReceive: HELOOOOOOO")
        val id = intent.getLongExtra(ID_KEY, 0)
        var reminder = ""

        val notificationUtils = NotificationUtils(context)
        val manager = notificationUtils.getManager()
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        Log.d("Maps",id.toString())
        when(geofencingEvent.geofenceTransition){
            Geofence.GEOFENCE_TRANSITION_ENTER -> reminder = "You have entered a Geofenced area"
            Geofence.GEOFENCE_TRANSITION_EXIT -> reminder = "You have exited a Geofenced area"
        }
        val notification = notificationUtils.getNotificationBuilder(reminder).build()
        manager.notify(id.hashCode(), notification)

        val count = manager.activeNotifications.size
        val summary = notificationUtils.getNotificationBuilder()
            .setGroupSummary(true)
            .setNumber(count)
            .build()
        manager.notify(0, summary)

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("MapsActivity", errorMessage + "BROADCAST FAIL")
            return
        }
    }

    companion object {
        const val ID_KEY = "ID_KEY"
    }



}