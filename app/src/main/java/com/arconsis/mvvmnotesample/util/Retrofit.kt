package com.arconsis.mvvmnotesample.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Alexander on 05.05.2017.
 */
fun retrofit(): Retrofit {
    return Retrofit.Builder()
            .baseUrl("http://192.168.2.190:8080/mvvm-note-backend-1.0-SNAPSHOT/api/note/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}