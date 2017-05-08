package com.arconsis.mvvmnotesample.login

import com.arconsis.mvvmnotesample.data.UserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Alexander on 04.05.2017.
 */
interface LoginApi {
    @FormUrlEncoded
    @POST("login")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<UserResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(@Field("username") username: String, @Field("password") password: String): Call<UserResponse>
}