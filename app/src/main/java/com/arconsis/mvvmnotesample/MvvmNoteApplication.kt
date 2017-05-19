package com.arconsis.mvvmnotesample

import android.app.Application
import com.arconsis.mvvmnotesample.notes.sync.NotesSyncRepository
import com.arconsis.mvvmnotesample.notes.sync.NotesSyncService

/**
 * Created by Alexander on 04.05.2017.
 */
class MvvmNoteApplication : Application() {

    val notesSyncService: NotesSyncRepository by lazy {
        NotesSyncService(applicationContext)
    }
}