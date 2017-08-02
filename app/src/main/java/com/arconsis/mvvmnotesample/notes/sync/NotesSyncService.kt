package com.arconsis.mvvmnotesample.notes.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager

/**
 * Created by akoufatzis on 19.05.17.
 */
class NotesSyncService(val context: Context) : NotesSyncRepository {

    private var notesUpdatedReceiver: NotesUpdatedReceiver? = null

    override fun schedule() {
        NotesGcmTaskService.schedule(context)
    }

    override fun unschedule() {
        NotesGcmTaskService.unschedule(context)
    }

    override fun notify(notificationHandler: (() -> Unit)?) {
        if (notificationHandler != null && notesUpdatedReceiver == null) {
            notesUpdatedReceiver = NotesUpdatedReceiver(notificationHandler)
            LocalBroadcastManager.getInstance(context)
                    .registerReceiver(notesUpdatedReceiver, IntentFilter(NotesGcmTaskService.BROADCASTS_NOTES_UPDATED))
        } else if (notesUpdatedReceiver != null) {
            notesUpdatedReceiver?.update = null
            LocalBroadcastManager.getInstance(context).unregisterReceiver(notesUpdatedReceiver)
        }
    }

    class NotesUpdatedReceiver(var update: (() -> Unit)?) : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            update?.invoke()
        }
    }
}