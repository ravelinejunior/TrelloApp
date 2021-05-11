package br.com.trelloapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }

    }

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(uri: Uri?, activity: Activity): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    //User Firebase variables
    const val USER_COLLECTION_NAME = "Users"
    const val USER_KEY_MODEL = "USER_KEY_MODEL"
    const val IMAGE_REFERENCE_DOCUMENT = "User_Image"
    const val IMAGE_USER_KEY = "image"
    const val NAME_USER_KEY = "name"
    const val MOBILE_USER_KEY = "mobile"
    const val EMAIL_USER_KEY = "email"
    const val ASSIGNED_TO_KEY = "assignedTo"

    const val PICK_IMAGE_REQUEST_CODE = 1615

    //Boards
    const val BOARDS_KEY_NAME_COLLECTION = "Boards"
    const val BOARD_IMAGE_REF = "Board_Image"
    const val BOARD_DETAIL = "board_detail"
    const val BOARD_MODEL_ID = "id"

    //Members List
    const val USER_MEMBER_ID = "id"

    //Task
    const val TASK_LIST: String = "taskList"
    const val TASK_LIST_ITEM_POSITION = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION = "card_list_item_position"

}