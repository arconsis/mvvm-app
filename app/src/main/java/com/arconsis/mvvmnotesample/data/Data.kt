package com.arconsis.mvvmnotesample.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import org.droitateddb.entity.Column
import org.droitateddb.entity.Entity
import org.droitateddb.entity.PrimaryKey

/**
 * Created by Alexander on 04.05.2017.
 */
data class User(val id: Int, val username: String, val password: String) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString(), parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(username)
        dest.writeString(password)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Creator<User> {
            override fun newArray(size: Int): Array<User> = emptyArray()

            override fun createFromParcel(source: Parcel) = User(source)
        }
    }
}

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
        val CREATOR = object : Creator<NoteDto> {
            override fun createFromParcel(source: Parcel): NoteDto = NoteDto(source)

            override fun newArray(size: Int): Array<NoteDto> = emptyArray()
        }
    }

}

data class Result<out T>(val success: Boolean, val value: T? = null) {
    companion object {
        fun <T> success(value: T) = Result(true, value)

        fun <T> failure() = Result<T>(false)
    }
}

data class UserResponse(var code: Int, var id: Int)

data class NotesResponse(val notes: List<NoteDto>)


@Entity
data class NoteDb(
        @PrimaryKey
        @Column
        var id: Int?,
        @Column
        var title: String,
        @Column
        var message: String,
        @Column
        var userId: Int) {
    constructor() : this(-1, "", "", -1)
}