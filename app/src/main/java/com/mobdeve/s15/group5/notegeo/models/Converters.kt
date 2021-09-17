package com.mobdeve.s15.group5.notegeo.models

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?) = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time

    @TypeConverter
    fun latLngToString(latLng: LatLng?) = latLng?.run { "$latitude $longitude" }

    @TypeConverter
    fun stringToLatLng(value: String?) = value?.let {
        val temp = it.split(" ")
        LatLng(temp[0].toDouble(), temp[1].toDouble())
    }
}