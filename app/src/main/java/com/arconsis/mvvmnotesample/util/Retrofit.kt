package com.arconsis.mvvmnotesample.util

import android.util.Log
import com.arconsis.mvvmnotesample.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Alexander on 05.05.2017.
 */
fun retrofit(): Retrofit {
    val url = BuildConfig.SERVER_URL + "api/note/"
    return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}