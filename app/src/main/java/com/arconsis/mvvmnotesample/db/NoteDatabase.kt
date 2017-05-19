package com.arconsis.mvvmnotesample.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by Alexander on 19.05.2017.
 */
@Database(entities = arrayOf(NoteDb::class), version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

fun createNoteDatabase(context: Context): NoteDatabase = Room.databaseBuilder(context, NoteDatabase::class.java, "note_db").build()