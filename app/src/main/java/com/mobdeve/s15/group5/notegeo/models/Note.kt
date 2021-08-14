package com.mobdeve.s15.group5.notegeo.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var _id: Long = 0,
    var title: String = "",
    var body: String = "",
    var color: Int = DEFAULT_COLOR,
    var label: Int = NO_LABEL,
    var isPinned: Boolean = false,
    var dateEdited: Date = Date(),
    var dateDeleted: Date? = null
) : Parcelable {
    val isBlank
        get() = title.isBlank() && body.isBlank()

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt() == 1,
        Date(parcel.readLong()),
        (parcel.readValue(Long::class.java.classLoader) as? Long)?.let { Date(it) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_id)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeInt(color)
        parcel.writeInt(label)
        parcel.writeInt(if (isPinned) 1 else 0)
        parcel.writeLong(dateEdited.time)
        dateDeleted?.time?.let { parcel.writeLong(it) }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Note> {
        const val DEFAULT_COLOR = -15262682
        const val NO_LABEL = -999

        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}
