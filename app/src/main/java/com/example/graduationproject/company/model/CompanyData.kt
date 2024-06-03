package com.example.graduationproject.company.model

import android.os.Parcel
import android.os.Parcelable

data class CompanyData(val Name:String, val Address:String,
                       val Telephones:String,val Classification:String,
                       val Branches: String,val KeyWords:String,
                       val About:String,val Fax:String,
                       val HotLine:String,val TaxNumber:String):Parcelable {
    constructor() : this("", "", "", "", "","","","","","")
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Name)
        parcel.writeString(Address)
        parcel.writeString(Telephones)
        parcel.writeString(Classification)
        parcel.writeString(Branches)
        parcel.writeString(KeyWords)
        parcel.writeString(About)
        parcel.writeString(Fax)
        parcel.writeString(HotLine)
        parcel.writeString(TaxNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CompanyData> {
        override fun createFromParcel(parcel: Parcel): CompanyData {
            return CompanyData(parcel)
        }
        override fun newArray(size: Int): Array<CompanyData?> {
            return arrayOfNulls(size)
        }
    }

}
