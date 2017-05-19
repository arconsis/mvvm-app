package com.arconsis.mvvmnotesample.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "Note")
data class NoteDb(
        @PrimaryKey(autoGenerate = true)
        var id: Int?,
        var title: String,
        var message: String,
        var userId: Int)
