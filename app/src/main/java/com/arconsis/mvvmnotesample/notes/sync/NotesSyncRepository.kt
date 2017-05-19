package com.arconsis.mvvmnotesample.notes.sync

/**
 * Created by akoufatzis on 18.05.17.
 */
interface NotesSyncRepository {

    fun schedule()
    fun unschedule()
    fun notify(notificationHandler: (() -> Unit)?)
}