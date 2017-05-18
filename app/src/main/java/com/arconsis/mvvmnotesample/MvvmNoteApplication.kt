package com.arconsis.mvvmnotesample

import android.app.Application
import com.arconsis.mvvmnotesample.sync.NotesBackgroundSync
import com.arconsis.mvvmnotesample.sync.NotesSyncRepository
import org.androidobjectherder.ObjectHerder

/**
 * Created by Alexander on 04.05.2017.
 */
class MvvmNoteApplication : Application() {

    val notesBackgroundSync: NotesSyncRepository by lazy {
        NotesBackgroundSync(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        ObjectHerder.startHerding(this)
    }
}