package com.arconsis.mvvmnotesample.sync

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
        notesUpdatedReceiver = NotesUpdatedReceiver(null)
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(notesUpdatedReceiver, IntentFilter(NotesGcmTaskService.BROADCASTS_NOTES_UPDATED))
        NotesGcmTaskService.schedule(context)
    }

    override fun unschedule() {
        NotesGcmTaskService.unschedule(context)
        val receiver = notesUpdatedReceiver
        if(receiver != null){
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
            receiver.update = null
        }
    }

    override fun notify(notificationHandler: (() -> Unit)?) {
        notesUpdatedReceiver?.update = notificationHandler
    }

    class NotesUpdatedReceiver(var update: (() -> Unit)?) : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            update?.invoke()
        }
    }
}