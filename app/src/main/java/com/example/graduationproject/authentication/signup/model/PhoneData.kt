package com.example.graduationproject.authentication.signup.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider

data class PhoneData(
    var token: PhoneAuthProvider.ForceResendingToken,
    var verificationId: String,

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(PhoneAuthProvider.ForceResendingToken::class.java.classLoader)!!,
        parcel.readString().toString(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(token, flags)
        parcel.writeString(verificationId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhoneData> {
        override fun createFromParcel(parcel: Parcel): PhoneData {
            return PhoneData(parcel)
        }

        override fun newArray(size: Int): Array<PhoneData?> {
            return arrayOfNulls(size)
        }
    }

}
