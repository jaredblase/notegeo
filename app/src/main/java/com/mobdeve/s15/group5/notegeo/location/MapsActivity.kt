package com.mobdeve.s15.group5.notegeo.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.databinding.ActivityMapsBinding
import com.mobdeve.s15.group5.notegeo.databinding.InputDialogBinding
import com.mobdeve.s15.group5.notegeo.editor.EditNoteActivity
import com.mobdeve.s15.group5.notegeo.toast

@SuppressLint("UnspecifiedImmutableFlag")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var circle: Circle? = null
    private var marker: Marker? = null
    private var geofence: Geofence? = null

    private var coordinates: LatLng? = null
    private var radius = 1.0
    private var noteId = 0L

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(
            this.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000 //10 seconds
        fastestInterval = 5000 //5 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMapsBinding.inflate(layoutInflater).root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        geofencingClient = LocationServices.getGeofencingClient(applicationContext)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(intent) {
            noteId = getLongExtra(NOTE_ID, 0)
            coordinates = getParcelableExtra(LAT_LNG)
            radius = getDoubleExtra(RADIUS, 1.0)

        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationSettings()) {
            startLocationUpdates()
        }
    }

    //Initializes google maps on current location
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val zoomLevel = 15f

        if (coordinates == null) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    val currLatLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, zoomLevel))
                }
                .addOnFailureListener {
                    Log.d("MapsActivity", "onFailure ${it.localizedMessage}")
                }
        } else {
            Log.d("MapsActivity", "onMapReady: COORDINATES SAVED")

            coordinates!!.let {
                toast("RADIUS: $radius")
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, zoomLevel))
                marker = map.addMarker(MarkerOptions().position(it))
                circle = map.addCircle(
                    CircleOptions().center(it)
                        .radius(radius)
                        .fillColor(ContextCompat.getColor(this, R.color.transparent))
                        .strokeColor(ContextCompat.getColor(this, R.color.note_color_7))
                )
            }
        }
        setMarker(map)
        enableMyLocation()
    }

    private fun isPermissionGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    // Shows user's location on map
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            Log.d("MapsActivity", "Permission needed")
        } else {
            toast("You have all the permission needed")
        }
    }

    //Adds a marker and its geofence circle
    private fun setMarker(map: GoogleMap) {
        map.setOnMapLongClickListener {
            val radBinding = InputDialogBinding.inflate(layoutInflater).apply {
                dialogTitle.text = getString(R.string.input_radius)
                input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }

            AlertDialog.Builder(this)
                .setView(radBinding.root)
                .setPositiveButton("Set") { _, _ ->
                    radius = try {
                        radBinding.input.text.toString().toDouble()
                    } catch (e: NumberFormatException) {
                        10.0
                    }
                    marker?.remove()
                    addMarker(it)
                    addCircle(it, radius)
                    addGeofence(it, radius)
                    coordinates = it
                    toast("Radius: $radius")
                }
                .setNegativeButton("Cancel") { _, _ -> radius = 10.0 }
                .create().show()
        }
    }

    private fun addMarker(latLng: LatLng) {
        marker = map.addMarker(MarkerOptions().position(latLng))
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

    @SuppressLint("MissingPermission")
    private fun addGeofence(latLng: LatLng, radius: Double) {
        coordinates?.run { removeGeofence() }
        geofence = getGeofence(latLng, radius)

        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
            addOnSuccessListener { Log.d("MapsActivity", "Geofence Added") }
            addOnFailureListener { Log.d("MapsActivity", it.stackTraceToString()) }
            addOnCompleteListener { Log.d("MapsActivity", "GEOFENCE ACTIVATED") }
        }
    }

    private fun removeGeofence() = geofencingClient.removeGeofences(geofencePendingIntent).run {
        addOnSuccessListener { Log.d("MapsActivity", "Geofence Deleted") }
    }

    private fun getGeofence(latLng: LatLng, radius: Double) = Geofence.Builder()
        .setCircularRegion(latLng.latitude, latLng.longitude, radius.toFloat())
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        .setRequestId(noteId.toString())
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .build()

    private fun getGeofencingRequest() = GeofencingRequest.Builder().apply {
        setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        geofence?.let { addGeofence(it) }
    }.build()


    override fun onBackPressed() {
        setResult(RESULT_OK, Intent(this, EditNoteActivity::class.java).apply {
            putExtra(LAT_LNG, coordinates)
            putExtra(RADIUS, radius)
        })
        finish()
    }

    private fun checkLocationSettings(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    companion object {
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val LOCATION_PERMISSION_INDEX = 0
        const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
        const val REQUEST_LOCATION_PERMISSION = 1
        const val RADIUS = "RADIUS"
        const val LAT_LNG = "LAT_LNG"
        const val NOTE_ID = "NOTE_ID"
    }
}