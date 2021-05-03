package br.com.trelloapp.model

import android.os.Parcel
import android.os.Parcelable

data class BoardModel(
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    val createdAtDate: Long = 0L,
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = "",
    var taskList: ArrayList<TaskModel> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.createStringArrayList()!!,
        parcel.readString().toString(),
        parcel.createTypedArrayList(TaskModel.CREATOR)!!

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeString(createdAt)
        parcel.writeLong(createdAtDate)
        parcel.writeStringList(assignedTo)
        parcel.writeString(documentId)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BoardModel> {
        override fun createFromParcel(parcel: Parcel): BoardModel {
            return BoardModel(parcel)
        }

        override fun newArray(size: Int): Array<BoardModel?> {
            return arrayOfNulls(size)
        }
    }
}