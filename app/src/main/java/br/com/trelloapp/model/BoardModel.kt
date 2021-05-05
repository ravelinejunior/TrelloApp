package br.com.trelloapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.android.gms.common.internal.safeparcel.SafeParcelable

@Keep
@SafeParcelable.Constructor
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
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
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

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BoardModel> = object : Parcelable.Creator<BoardModel> {
            override fun createFromParcel(source: Parcel): BoardModel = BoardModel(source)
            override fun newArray(size: Int): Array<BoardModel?> = arrayOfNulls(size)
        }
    }
}