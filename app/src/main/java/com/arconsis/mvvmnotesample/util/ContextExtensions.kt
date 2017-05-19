package com.arconsis.mvvmnotesample.util

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import com.arconsis.mvvmnotesample.MvvmNoteApplication
import com.arconsis.mvvmnotesample.db.NoteDao
import com.arconsis.mvvmnotesample.db.createNoteDatabase
import com.arconsis.mvvmnotesample.notes.sync.NotesSyncRepository

/**
 * Created by Alexander on 04.05.2017.
 */
fun Context.toast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_LONG) {
    appContext().toast(text, duration)
}

fun Fragment.appContext(): Context = this.context.applicationContext


val Context.notesSyncRepository: NotesSyncRepository
    get() = (applicationContext as MvvmNoteApplication).notesSyncService

val Context.networkChecker: NetworkChecker
    get() = NetworkChecker(applicationContext)


val Context.noteDao: NoteDao
    get() = createNoteDatabase(applicationContext).noteDao()