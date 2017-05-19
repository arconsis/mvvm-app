package com.arconsis.mvvmnotesample

import android.app.Application
import com.arconsis.mvvmnotesample.sync.NotesSyncService
import com.arconsis.mvvmnotesample.sync.NotesSyncRepository
import org.androidobjectherder.ObjectHerder

/**
 * Created by Alexander on 04.05.2017.
 */
class MvvmNoteApplication : Application() {

    val notesSyncService: NotesSyncRepository by lazy {
        NotesSyncService(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        ObjectHerder.startHerding(this)
    }
}