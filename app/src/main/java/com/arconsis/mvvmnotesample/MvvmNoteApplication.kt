package com.arconsis.mvvmnotesample

import android.app.Application
import org.androidobjectherder.ObjectHerder

/**
 * Created by Alexander on 04.05.2017.
 */
class MvvmNoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectHerder.startHerding(this)
    }
}