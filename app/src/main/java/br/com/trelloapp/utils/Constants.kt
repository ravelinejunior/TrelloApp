package br.com.trelloapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

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

    //User Firebase variables
    const val USER_COLLECTION_NAME = "Users"
    const val USER_KEY_MODEL = "USER_KEY_MODEL"
    const val IMAGE_REFERENCE_DOCUMENT = "User_Image"
    const val IMAGE_USER_KEY = "image"
    const val NAME_USER_KEY = "name"
    const val MOBILE_USER_KEY = "mobile"

}