package com.arconsis.mvvmnotesample.util

import android.content.Context
import android.net.ConnectivityManager

class NetworkChecker(private val context: Context) {
    fun isNetworkAvailable(): Boolean {
        val mgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = mgr.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}