package com.arconsis.mvvmnotesample.notes

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.arconsis.mvvmnotesample.data.getLocalUser
import com.arconsis.mvvmnotesample.data.isLocalUserPresent
import com.arconsis.mvvmnotesample.db.NoteDb
import com.arconsis.mvvmnotesample.util.NetworkChecker
import com.google.android.gms.gcm.*
import org.droitateddb.EntityService

/**
 * Created by Alexander on 09.05.2017.
 */
class NotesBackgroundSync : GcmTaskService() {

    private val notesService by lazy {
        NoteService(EntityService<NoteDb>(applicationContext, NoteDb::class.java), NetworkChecker(applicationContext))
    }

    override fun onInitializeTasks() {
        super.onInitializeTasks()
        schedule(this)
    }

    override fun onRunTask(param: TaskParams?): Int {
        if (isLocalUserPresent()) {
            val localUser = getLocalUser()
            notesService.getNotesForUser(localUser).blockingFirst()
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(BROADCASTS_NOTES_UPDATED))
            return GcmNetworkManager.RESULT_SUCCESS
        } else {
            return GcmNetworkManager.RESULT_SUCCESS
        }
    }

    companion object {
        val BROADCASTS_NOTES_UPDATED = "notes_updated"

        private val INTERVAL_IN_S = 24 * 60 * 60.toLong()
        private val SERVICE_TAG = NotesBackgroundSync::class.java.canonicalName

        fun schedule(context: Context) {
            val task = PeriodicTask.Builder()
                    .setPeriod(INTERVAL_IN_S)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setPersisted(true)
                    .setService(NotesBackgroundSync::class.java)
                    .setTag(SERVICE_TAG)
                    .setUpdateCurrent(true)
                    .build()

            GcmNetworkManager.getInstance(context).schedule(task)
        }

        fun unschedule(context: Context) {
            GcmNetworkManager.getInstance(context).cancelTask(SERVICE_TAG, NotesBackgroundSync::class.java)
        }
    }
}