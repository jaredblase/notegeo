package com.mobdeve.s15.group5.notegeo.models

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.ObservableBoolean
import androidx.room.*
import java.util.*

@Entity(indices = [Index(value = ["label"], unique = true)])
data class Label(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    var label: String = "",
) : Parcelable {
    /** Used for the label activity */
    @Ignore var isChecked = ObservableBoolean()
    @Ignore val isBeingEdited = ObservableBoolean()

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
    )

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(_id)
        parcel.writeString(label)
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
    var dateDeleted: Date? = null
) : Parcelable {
    val isBlank
        get() = title.isBlank() && body.isBlank()

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readSerializable() as? Int,
        parcel.readInt() == 1,
        Date(parcel.readLong()),
        (parcel.readSerializable() as? Long)?.let { Date(it) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_id)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeInt(color)
        parcel.writeSerializable(label)
        parcel.writeInt(if (isPinned) 1 else 0)
        parcel.writeLong(dateEdited.time)
        parcel.writeSerializable(dateDeleted?.time)
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
        override fun createFromParcel(parcel: Parcel): NoteAndLabel {
            return NoteAndLabel(parcel)
        }

        override fun newArray(size: Int): Array<NoteAndLabel?> {
            return arrayOfNulls(size)
        }
    }

}
