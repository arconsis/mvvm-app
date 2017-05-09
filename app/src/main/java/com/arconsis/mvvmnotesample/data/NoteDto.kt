package com.arconsis.mvvmnotesample.data

import android.os.Parcel
import android.os.Parcelable

data class NoteDto(var id: Int, var title: String, var message: String, var userId: Int) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(title)
        dest.writeString(message)
        dest.writeInt(userId)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<NoteDto> {
            override fun createFromParcel(source: Parcel): NoteDto = NoteDto(source)

            override fun newArray(size: Int): Array<NoteDto> = emptyArray()
        }
    }

}