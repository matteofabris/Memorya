package com.memorya.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import timber.log.Timber

fun Activity.checkInternetConnection(): Boolean {
    //Check internet connection:
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Timber.i("NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Timber.i("NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Timber.i("NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }

    return false
}

fun Activity.getBooleanPref(key: String): Boolean {
    return getPreferences(Context.MODE_PRIVATE).getBoolean(key, false)
}

fun Activity.setBooleanPref(key: String, value: Boolean) {
    with(getPreferences(Context.MODE_PRIVATE).edit()) {
        putBoolean(key, value)
        apply()
    }
}