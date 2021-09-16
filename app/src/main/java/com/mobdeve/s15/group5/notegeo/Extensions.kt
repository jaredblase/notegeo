package com.mobdeve.s15.group5.notegeo

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

fun View.dpToPx(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5F).toInt()
}

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.buildConfirmationDialog(message: String, posAction: DialogInterface.OnClickListener) =
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton("DELETE", posAction)
        .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
        .create()

fun AppCompatActivity.focusAndOpenKeyboard(v: View) {
    v.requestFocus()
    with(getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager) {
        showSoftInput(v, 0)
    }
}

fun AppCompatActivity.hideKeyboard(v: View) {
    with(getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun LatLng.formatString(radius: Double) = with (DecimalFormat("0.00")) {
    val lat = format(latitude)
    val long = format(longitude)
    "Latitude: $lat, Longitude: $long, $radius"
}

class MyWatcher(private val fn: (Editable?) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) = fn(s)
}