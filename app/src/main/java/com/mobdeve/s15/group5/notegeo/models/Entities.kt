package com.mobdeve.s15.group5.notegeo.models

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.ObservableBoolean
import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Label(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    var name: String = "",
) : Parcelable {
    /** Used for the label activity */
    @Ignore
    var isChecked = ObservableBoolean()
    @Ignore
    val isBeingEdited = ObservableBoolean()

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
    )

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(_id)
        parcel.writeString(name)
    }

    companion object CREATOR : Parcelable.Creator<Label> {
        override fun createFromParcel(parcel: Parcel): Label {
            return Label(parcel)
        }

        override fun newArray(size: Int): Array<Label?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var _id: Long = 0,
    var title: String = "",
    var body: String = "",
    var color: Int = DEFAULT_COLOR,
    var label: Int? = null,
    var isPinned: Boolean = false,
    var dateEdited: Date = Date(),
    var dateDeleted: Date? = null,
    var dateAlarm: Date? = null,
    var coordinates: LatLng? = null,
    var radius: Double = 10.0,
    var locName: String = ""
) : Parcelable {
    val isBlank
        get() = title.isBlank() && body.isBlank()

    val hasReminders
        get() = dateAlarm != null || coordinates != null

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readSerializable() as? Int,
        parcel.readInt() == 1,
        Date(parcel.readLong()),
        (parcel.readSerializable() as? Long)?.let { Date(it) },
        (parcel.readSerializable() as? Long)?.let { Date(it) },
        parcel.readParcelable(LatLng::class.java.classLoader),
        parcel.readDouble(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeLong(_id)
        writeString(title)
        writeString(body)
        writeInt(color)
        writeSerializable(label)
        writeInt(if (isPinned) 1 else 0)
        writeLong(dateEdited.time)
        writeSerializable(dateDeleted?.time)
        writeSerializable(dateAlarm?.time)
        writeParcelable(coordinates, flags)
        writeDouble(radius)
        writeString(locName)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Note> {
        const val DEFAULT_COLOR = -15262682
        override fun createFromParcel(parcel: Parcel) = Note(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Note?>(size)
    }
}

data class NoteAndLabel(
    @Embedded var note: Note = Note(),
    @Relation(
        parentColumn = "label",
        entityColumn = "_id"
    )
    var label: Label? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Note::class.java.classLoader)!!,
        parcel.readParcelable(Label::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(note, flags)
        parcel.writeParcelable(label, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<NoteAndLabel> {
        override fun createFromParcel(parcel: Parcel) = NoteAndLabel(parcel)
        override fun newArray(size: Int) = arrayOfNulls<NoteAndLabel?>(size)
    }
}
