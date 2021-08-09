package com.mobdeve.s15.group5.notegeo.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    var title: String = "",
    var body: String = "",
    var color: Int = DEFAULT_COLOR,
    var dateEdited: Date = Date(),
    var dateDeleted: Date? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        Date(parcel.readLong()),
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(_id)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeInt(color)
        parcel.writeLong(dateEdited.time)
        dateDeleted?.time?.let { parcel.writeLong(it) }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Note> {
        const val DEFAULT_COLOR = -15262682

        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}
