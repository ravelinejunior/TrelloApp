package br.com.trelloapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class TaskModel(
    var title: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    var cards:ArrayList<CardModel> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(CardModel.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeString(createdAt)
        parcel.writeTypedList(cards)
    }

    override fun describeContents() :Int = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TaskModel> = object : Parcelable.Creator<TaskModel> {
            override fun createFromParcel(source: Parcel): TaskModel = TaskModel(source)
            override fun newArray(size: Int): Array<TaskModel?> = arrayOfNulls(size)
        }
    }
}