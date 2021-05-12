package br.com.trelloapp.model

import android.os.Parcel
import android.os.Parcelable

data class SelectedMembersModel(
    val id: String = "",
    val image: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) 
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SelectedMembersModel> {
        override fun createFromParcel(parcel: Parcel): SelectedMembersModel {
            return SelectedMembersModel(parcel)
        }

        override fun newArray(size: Int): Array<SelectedMembersModel?> {
            return arrayOfNulls(size)
        }
    }
}