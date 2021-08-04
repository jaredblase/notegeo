package com.mobdeve.s15.group5.notegeo.models

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["label"], unique = true)])
data class Label(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    var label: String = "",
    /** Used for the label activity */
    @Ignore var isChecked: ObservableBoolean = ObservableBoolean()
)
