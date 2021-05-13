package br.com.trelloapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class CardModel(
    val name: String = "",
    val createdBy: String = "",
    val createAt: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var labelColor: String = "",
    val dueDate: Long = 0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(createdBy)
        parcel.writeString(createAt)
        parcel.writeStringList(assignedTo)
        parcel.writeString(labelColor)
        parcel.writeLong(dueDate)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CardModel> = object : Parcelable.Creator<CardModel> {
            override fun createFromParcel(source: Parcel): CardModel = CardModel(source)
            override fun newArray(size: Int): Array<CardModel?> = arrayOfNulls(size)
        }
    }
}
