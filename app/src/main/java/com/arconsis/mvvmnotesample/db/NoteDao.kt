package com.arconsis.mvvmnotesample.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created by Alexander on 19.05.2017.
 */
@Dao
interface NoteDao {
    @Query("Select * From Note")
    fun getAllNotes(): List<NoteDb>

    @Delete
    fun deleteNotes(notes: List<NoteDb>)

    @Insert
    fun addNotes(notes: List<NoteDb>)

    @Insert
    fun addNote(noteDb: NoteDb)
}