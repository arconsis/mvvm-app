package com.arconsis.mvvmnotesample.notes

import android.util.Log
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.androidobjectherder.HerdedObjectLifecycle

/**
 * Created by Alexander on 05.05.2017.
 */
class NotesViewModel(private val user: User, val noteService: NoteService) : HerdedObjectLifecycle {
    val disposable = CompositeDisposable()
    var actions: NotesActions? = null

    fun loadNotesForCurrentUser() {
        noteService.getNotesForUser(user)
                .subscribe(this::onNotesAvailable, this::onError, {}, this::onSubscribe)
    }

    fun createNewNote() {
        actions?.onCreateNewNote()
    }

    fun logout() {
        noteService.clearNotes()
    }


    fun readLocalNotes() {
        val localNotes = noteService.readNotesFromLocalDatabase()
        actions?.onNotesAvailable(localNotes)
    }

    override fun unherderd() {
        disposable.dispose()
    }

    private fun onSubscribe(d: Disposable) {
        disposable.add(d)
    }

    private fun onError(t: Throwable) {
        Log.e(TAG, "Error", t)
    }

    private fun onNotesAvailable(notes: List<NoteDto>) {
        actions?.onNotesAvailable(notes)
    }

    interface NotesActions {
        fun onNotesAvailable(notes: List<NoteDto>)
        fun onCreateNewNote()
        fun onFailure()
    }

    companion object {
        private val TAG = NotesViewModel::class.java.simpleName
    }
}

