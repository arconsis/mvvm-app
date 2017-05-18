package com.arconsis.mvvmnotesample.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.arconsis.mvvmnotesample.data.getLocalUser
import com.arconsis.mvvmnotesample.data.isLocalUserPresent
import com.arconsis.mvvmnotesample.db.NoteDb
import com.arconsis.mvvmnotesample.notes.NoteService
import com.arconsis.mvvmnotesample.util.NetworkChecker
import com.google.android.gms.gcm.*
import io.reactivex.android.schedulers.AndroidSchedulers
import org.droitateddb.EntityService
import java.lang.Exception

/**
 * Created by Alexander on 09.05.2017.
 */
class NotesBackgroundSync(val context: Context) : GcmTaskService(), NotesSyncRepository {

    private var noteUpdatedReceiver: NotesUpdatedReceiver? = NotesUpdatedReceiver(this::notesUpdated)
    private var notificationHandler: (() -> Unit)? = null
    val BROADCASTS_NOTES_UPDATED = "notes_updated"

    init {
        LocalBroadcastManager.getInstance(context).registerReceiver(noteUpdatedReceiver, IntentFilter(BROADCASTS_NOTES_UPDATED))
    }


    private val INTERVAL_IN_S = 24 * 60 * 60.toLong()
    private val LOG_TAG = NotesBackgroundSync::class.java.simpleName
    private val SERVICE_TAG = NotesBackgroundSync::class.java.canonicalName

    private val notesService by lazy {
        NoteService(EntityService<NoteDb>(applicationContext, NoteDb::class.java), NetworkChecker(applicationContext), AndroidSchedulers.mainThread())
    }

    override fun onInitializeTasks() {
        super.onInitializeTasks()
        schedule()
    }

    override fun onRunTask(param: TaskParams?): Int {
        if (isLocalUserPresent()) {
            val localUser = getLocalUser()
            try {
                notesService.getNotesForUser(localUser).blockingFirst()
                LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(BROADCASTS_NOTES_UPDATED))
                return GcmNetworkManager.RESULT_SUCCESS
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error while syncing", e)
                return GcmNetworkManager.RESULT_FAILURE
            }
        } else {
            return GcmNetworkManager.RESULT_SUCCESS
        }
    }

    override fun schedule() {
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

    override fun unschedule() {
        GcmNetworkManager.getInstance(context).cancelTask(SERVICE_TAG, NotesBackgroundSync::class.java)
        LocalBroadcastManager.getInstance(context).unregisterReceiver(noteUpdatedReceiver)
        noteUpdatedReceiver?.update = null
        noteUpdatedReceiver = null
    }

    fun notesUpdated(){
        notificationHandler?.invoke()
    }

    override fun notify(notificationHandler: (() -> Unit)?) {
        // TODO: when null is passed could unregister receiver too
        this.notificationHandler = notificationHandler
    }

    private class NotesUpdatedReceiver(var update: (() -> Unit)?) : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            update?.invoke()
        }
    }
}