package com.arconsis.mvvmnotesample.notes

import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.NotesResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Alexander on 05.05.2017.
 */
interface NoteApi {
    @GET("{userId}")
    fun getNotesByUserId(@Path("userId") userId: Int): Call<NotesResponse>

    @FormUrlEncoded
    @POST("{userId}/create")
    fun createNote(@Field("title") title: String, @Field("message") message: String, @Path("userId") userId: Int): Call<NoteDto>
}