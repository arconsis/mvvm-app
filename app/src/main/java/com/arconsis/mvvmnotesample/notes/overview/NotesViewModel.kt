package com.arconsis.mvvmnotesample.notes.overview

import android.arch.lifecycle.ViewModel
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.notes.NoteService
import com.arconsis.mvvmnotesample.notes.sync.NotesSyncRepository

/**
 * Created by Alexander on 05.05.2017.
 */
class NotesViewModel(private val user: User,
                     val noteService: NoteService,
                     private val notesSyncRepository: NotesSyncRepository) : ViewModel() {
    val disposable = io.reactivex.disposables.CompositeDisposable()

    var actions: com.arconsis.mvvmnotesample.notes.overview.NotesViewModel.NotesActions? = null
        set(value) {
            field = value
            if (value != null) {
                notesSyncRepository.notify(this::readLocalNotes)
            } else {
                notesSyncRepository.notify(null)
            }
        }

    fun loadNotesForCurrentUser() {
        noteService.getNotesForUser(user)
                .subscribe(this::onNotesAvailable, this::onError, {}, this::onSubscribe)
    }

    fun createNewNote() {
        actions?.onCreateNewNote()
    }

    fun logout() {
        notesSyncRepository.unschedule()
        noteService.clearNotes()
    }


    fun readLocalNotes() {
        val localNotes = noteService.readNotesFromLocalDatabase()
        actions?.onNotesAvailable(localNotes)
    }

    override fun onCleared() {
        disposable.dispose()
    }

    private fun onSubscribe(d: io.reactivex.disposables.Disposable) {
        disposable.add(d)
    }

    private fun onError(t: Throwable) {
        android.util.Log.e(com.arconsis.mvvmnotesample.notes.overview.NotesViewModel.Companion.TAG, "Error", t)
    }

    private fun onNotesAvailable(notes: List<com.arconsis.mvvmnotesample.data.NoteDto>) {
        actions?.onNotesAvailable(notes)
    }

    interface NotesActions {
        fun onNotesAvailable(notes: List<com.arconsis.mvvmnotesample.data.NoteDto>)
        fun onCreateNewNote()
        fun onFailure()
    }

    companion object {
        private val TAG = com.arconsis.mvvmnotesample.notes.overview.NotesViewModel::class.java.simpleName
    }
}

