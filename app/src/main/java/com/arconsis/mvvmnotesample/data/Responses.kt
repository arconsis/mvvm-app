package com.arconsis.mvvmnotesample.data

data class Result<out T>(val success: Boolean, val value: T? = null) {
    companion object {
        fun <T> success(value: T) = Result(true, value)

        fun <T> failure() = Result<T>(false)
    }
}

data class UserResponse(var code: Int, var id: Int)

data class NotesResponse(val notes: List<NoteDto>)


