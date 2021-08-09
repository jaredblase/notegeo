package com.mobdeve.s15.group5.notegeo.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    var title: String = "",
    var body: String = "",
    var color: Int = DEFAULT_COLOR,
    var dateDeleted: Date? = null
) {
    companion object {
        const val DEFAULT_COLOR = -15262682
    }
}
