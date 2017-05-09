package com.arconsis.mvvmnotesample.data

import android.os.Parcel
import android.os.Parcelable

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
        val CREATOR = object : Parcelable.Creator<User> {
            override fun newArray(size: Int): Array<User> = emptyArray()

            override fun createFromParcel(source: Parcel) = User(source)
        }
    }
}