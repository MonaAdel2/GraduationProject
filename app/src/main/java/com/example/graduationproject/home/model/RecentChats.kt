package com.example.graduationproject.home.model

import android.os.Parcel
import android.os.Parcelable

data class RecentChat(
    val friendId: String? = "",
    val name: String? = "",
    val time: String? = "",
    val friendImage: String? = "",
    val sender: String? = "",
    val message: String? = "",
    val person: String? = "",
    val image: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(friendId)
        parcel.writeString(name)
        parcel.writeString(time)
        parcel.writeString(friendImage)
        parcel.writeString(sender)
        parcel.writeString(message)
        parcel.writeString(person)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentChat> {
        override fun createFromParcel(parcel: Parcel): RecentChat {
            return RecentChat(parcel)
        }

        override fun newArray(size: Int): Array<RecentChat?> {
            return arrayOfNulls(size)
        }
    }

}