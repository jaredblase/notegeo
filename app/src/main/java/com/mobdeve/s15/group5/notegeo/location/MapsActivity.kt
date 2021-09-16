package com.mobdeve.s15.group5.notegeo.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.databinding.ActivityMapsBinding
import com.mobdeve.s15.group5.notegeo.editor.NoteEditorViewModel
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory
import kotlinx.coroutines.Dispatchers

@SuppressLint("UnspecifiedImmutableFlag")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val model by viewModels<NoteEditorViewModel> {
        ViewModelFactory(
            (application as NoteGeoApplication).repo,
            Dispatchers.IO
        )
    }
    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var circle: Circle? = null
    private var marker: Marker? = null
    private var geofence: Geofence? = null


    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    //Initializes google maps on current location
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val zoomLevel = 15f
        if (model.noteAndLabel.note.coordinates == null) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val currLatLng = LatLng(location.latitude, location.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, zoomLevel))
                    } else {
                        Log.d("MapsActivity", "Location is null")
                    }
                }
                .addOnFailureListener {
                    Log.d("MapsActivity", "onFailure " + it.localizedMessage)
                }
        } else {
            Log.d("MapsActivity", "onMapReady: COORDINATES SAVED ")
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    model.noteAndLabel.note.coordinates,
                    zoomLevel+30
                )
            )
            map.addMarker(MarkerOptions().position(model.noteAndLabel.note.coordinates))
            map.addCircle(
                CircleOptions().center(model.noteAndLabel.note.coordinates)
                    .radius(model.noteAndLabel.note.radius + 50)
                    .fillColor(ContextCompat.getColor(this, R.color.transparent))
                    .strokeColor(ContextCompat.getColor(this, R.color.note_color_7))
            )
        }
        setMarker(map)
        enableMyLocation()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //Shows user's location on map
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            Log.d("MapsActivity", "Permission needed")
        } else {
            Toast.makeText(this, "You have all the permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    //Adds a marker and its geofence circle
    private fun setMarker(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            marker?.remove()
            addMarker(latLng)
            addCircle(latLng, model.noteAndLabel.note.radius + 50)
            addGeofence(latLng, model.noteAndLabel.note.radius + 50)
            model.noteAndLabel.note.coordinates = latLng

        }
    }

    private fun addMarker(latLng: LatLng) {
        marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
        )

    }

    private fun addCircle(latLng: LatLng, radius: Double) {
        circle?.remove()
        circle = map.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(ContextCompat.getColor(this, R.color.transparent))
                .strokeColor(ContextCompat.getColor(this, R.color.note_color_7))
        )
    }


    private fun addGeofence(latLng: LatLng, radius: Double) {
        if (model.noteAndLabel.note.coordinates != null) {
            removeGeofence()
        }

        geofence = getGeofence(latLng, radius)
        val geoRequest: GeofencingRequest = getGeofencingRequest()

        geofencingClient.addGeofences(geoRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("MapsActivity", "Geofence Added")
            }
            addOnFailureListener {
                Log.d("MapsActivity", it.stackTraceToString())
            }
            addOnCompleteListener {
                Log.d("MapsActivity", "GEOFENCE ACTIVATED")
            }
        }

    }

    private fun removeGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("MapsActivity", "Geofence Deleted")
            }
        }
    }


    private fun getGeofence(latLng: LatLng, radius: Double): Geofence {
        return Geofence.Builder()
            .setCircularRegion(
                latLng.latitude, latLng.longitude, radius.toFloat()
            )
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setRequestId(model.noteAndLabel.note._id.toString())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }


    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()

    }


    companion object {
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val LOCATION_PERMISSION_INDEX = 0
        const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
        const val GEOFENCE_RADIUS = 100.0
        const val REQUEST_LOCATION_PERMISSION = 1
        const val NEVER_EXPIRE = -1
    }


}