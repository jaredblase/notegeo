package com.mobdeve.s15.group5.notegeo.editor

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.mobdeve.s15.group5.notegeo.*
import com.mobdeve.s15.group5.notegeo.MapsActivity.Companion.REQUEST_TURN_DEVICE_LOCATION_ON
import com.mobdeve.s15.group5.notegeo.databinding.ActivityEditNoteBinding
import com.mobdeve.s15.group5.notegeo.home.MainActivity
import com.mobdeve.s15.group5.notegeo.models.NoteAndLabel
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import java.util.*

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private val model by viewModels<NoteEditorViewModel> {
        ViewModelFactory(
            (application as NoteGeoApplication).repo,
            Dispatchers.IO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set note to view model, if no note was passed, create a new note
        model.noteAndLabel = intent.getParcelableExtra(NOTE_AND_LABEL) ?: NoteAndLabel()

        // bind model with layout using observers
        model.selectedBackgroundColor.observe(this) { binding.root.setBackgroundColor(it) }
        model.dateEdited.observe(this) { binding.editorDateEditedTv.text = it }
        model.mPinned.observe(this) {
            binding.setPinnedBtn.setImageResource(if (it) R.drawable.ic_pin_filled else R.drawable.ic_pin)
        }

        // hide other buttons when editing
        model.isEditing.observe(this) {
            val value: Int
            val antiValue: Int

            if (it) {
                value = View.GONE
                antiValue = View.VISIBLE
            } else {
                value = View.VISIBLE
                antiValue = View.GONE
            }

            with(binding) {
                setPinnedBtn.visibility = value
                setAlarmBtn.visibility = value
                setLocationBtn.visibility = value
                editorMoreOptions.visibility = value
                editorSaveBtn.visibility = antiValue
                editorCancelBtn.visibility = antiValue
            }
        }

        // setup label observer
        model.labelName.observe(this) { updateTags(binding.labelTv, it) }

        // setup alarm observer
        model.dateAlarm.observe(this) {
            updateTags(binding.alarmTv, it)
            model.noteAndLabel.note.dateAlarm?.time?.let { time ->
                binding.alarmTv.paintFlags =
                    if (time <= System.currentTimeMillis()) Paint.STRIKE_THRU_TEXT_FLAG else 0
            }
        }

        binding.setAlarmBtn.setOnClickListener {
            // today forward constraint
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
            // instantiate date picker
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("SELECT ALARM DATE")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            // ok is pressed
            datePicker.addOnPositiveButtonClickListener {
                var date = datePicker.selection!!
                val cal = Calendar.getInstance()

                // instantiate time picker
                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(cal.get(Calendar.HOUR_OF_DAY))
                    .setMinute(cal.get(Calendar.MINUTE))
                    .setTitleText("SELECT ALARM TIME")
                    .build()

                // ok is pressed
                timePicker.addOnPositiveButtonClickListener {
                    date += (timePicker.hour - 8) * 3600000 // hours
                    date += timePicker.minute * 60000       // minutes

                    if (date - cal.timeInMillis < 60000) {
                        this.toast("Alarm must be set at least 1 minute from now!")
                    } else {
                        model.setDateAlarm(Date(date))
                    }
                }

                if (supportFragmentManager.findFragmentByTag(TIME_TAG) == null) {
                    timePicker.show(supportFragmentManager, TIME_TAG)
                }
            }

            if (supportFragmentManager.findFragmentByTag(DATE_TAG) == null) {
                datePicker.show(supportFragmentManager, DATE_TAG)
            }
        }

        // TODO: setup location listener
        binding.setLocationBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                checkLocationSettings()
                launchMaps()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    MapsActivity.REQUEST_LOCATION_PERMISSION
                )
            }
        }
        binding.locationTv.visibility = View.GONE

        val watcher = MyWatcher { model.isEditing.value = true }

        // note text editing
        refreshFields()
        binding.editorTitleEt.addTextChangedListener(watcher)
        binding.editorBodyEt.addTextChangedListener(watcher)

        binding.editorMoreOptions.setOnClickListener {
            // assures only menu will appear even with multiple clicks
            if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
                EditorMenuFragment().show(supportFragmentManager, FRAGMENT_TAG)
            }
        }

        with(binding) {
            editorSaveBtn.setOnClickListener { if (model.save(this)) clearFocus(it) }
            editorCancelBtn.setOnClickListener { refreshFields(); clearFocus(it) }
            setPinnedBtn.setOnClickListener { model.togglePin() }

            labelTv.setOnRemoveListener { model.assignLabel(null) }
            alarmTv.setOnRemoveListener { model.setDateAlarm(null) }
            locationTv.setOnRemoveListener { /* TODO: Clear Location */ }
        }
    }

    /**
     * Updates the received view with the received string.
     * Also adjusts the height of the body editor depending
     * if at least one tag is visible.
     */
    private fun updateTags(v: RemovableItemView, t: String?) {
        if (t == null) {
            v.visibility = View.GONE

            // check other tags
            with(binding) {
                if (labelTv.visibility == View.GONE && alarmTv.visibility == View.GONE && locationTv.visibility == View.GONE) {
                    tagsHolderFl.visibility = View.GONE
                    editorBodyEt.updateLayoutParams { height = 0 }
                }
            }
        } else {
            v.text = t
            v.visibility = View.VISIBLE

            with(binding) {
                if (tagsHolderFl.visibility != View.VISIBLE) {
                    tagsHolderFl.visibility = View.VISIBLE
                    editorBodyEt.updateLayoutParams { height = WRAP_CONTENT }
                }
            }
        }
    }

    private fun clearFocus(v: View) {
        hideKeyboard(v)
        binding.editorBodyEt.clearFocus()
        binding.editorTitleEt.clearFocus()
    }

    private fun refreshFields() {
        with(binding) {
            with(model.noteAndLabel.note) {
                editorTitleEt.setText(title)
                editorBodyEt.setText(body)
            }
        }
        model.isEditing.value = false
    }

    override fun onBackPressed() {
        if (model.isEditing.value == true) {
            refreshFields()
        } else if (!model.noteAndLabel.note.isBlank) {
            model.finalSave(applicationContext)
            setResult(RESULT_OK, Intent(this, MainActivity::class.java).apply {
                putExtra(NOTE_AND_LABEL, model.noteAndLabel)
            })
            finish()
        } else {
            toast("Blank note deleted!")
            super.onBackPressed()
        }
    }

    private val mapResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // a note was passed
                result.data?.getParcelableExtra<NoteAndLabel>(NOTE_AND_LABEL)?.let {
                    when (result.resultCode) {
                        else -> this.toast("Blank note deleted!")
                    }
            }
        }

    private fun launchMaps(note: NoteAndLabel = model.noteAndLabel) {
        mapResultLauncher.launch(Intent(this, MapsActivity::class.java).apply {
            putExtra(NOTE_AND_LABEL, note)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() ||
            grantResults[MapsActivity.LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == MapsActivity.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[MapsActivity.BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            Log.d("MapsActivity", "Permission needed")
        } else {
            Toast.makeText(this, "You have all the permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        this,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(
                        "EDITNOTE",
                        "Error getting location settings resolution: " + sendEx.message
                    )
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Location must be enabled", Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkLocationSettings()
                }.show()

            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Location can be accessed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkLocationSettings(false)
        }
    }


    companion object {
        private const val FRAGMENT_TAG = "Editor Menu"
        private const val DATE_TAG = "DatePicker"
        private const val TIME_TAG = "TimePicker"
        const val NOTE_AND_LABEL = "NoteAndLabel"
        const val DELETE = 100
        const val DUPLICATE = 200
    }
}