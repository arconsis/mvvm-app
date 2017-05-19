package com.arconsis.mvvmnotesample.notes.create

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.notes.NoteService
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Alexander on 08.05.2017.
 */
class CreateNoteViewModel(private val user: User, private val noteService: NoteService) : ViewModel() {
    var title = ""
    var message = ""
    var actions: CreateNoteActions? = null
        set(value) {
            field = value
            notifyNoteCreatedIfPresent()
        }

    var createdNote: NoteDto? = null
    private val disposables = CompositeDisposable()
    private var processing = false

    fun onAddImage() {

    }

    fun onCreateNote() {
        process {
            noteService.createNote(title, message, user)
                    .subscribe(this::onNoteCreated, this::onError, {}, this::onSubscribe)
        }
    }

    override fun onCleared() {
        disposables.dispose()
    }

    private fun onSubscribe(d: Disposable) {
        disposables.add(d)
    }

    private fun onError(t: Throwable) {
        Log.e(TAG, "Error", t)
    }

    private fun onNoteCreated(result: Result<NoteDto>) {
        if (result.success && result.value != null) {
            createdNote = result.value
            actions?.onNoteCreated(result.value)
        } else {
            actions?.onFailure()
        }
    }

    private fun process(block: () -> Unit) {
        if (isDataPresent()) {
            processing = true
            actions?.onProcessing()
            block()
        } else {
            actions?.onDataMissing()
        }
    }

    private fun isDataPresent(): Boolean = title.isNotEmpty() && message.isNotEmpty()

    private fun notifyNoteCreatedIfPresent() {
        val note = createdNote
        if (note != null) {
            actions?.onNoteCreated(note)
        }
    }


    interface CreateNoteActions {
        fun onNoteCreated(note: NoteDto)
        fun onFailure()
        fun onProcessing()
        fun onDataMissing()
    }

    companion object {
        private val TAG = CreateNoteViewModel::class.java.simpleName
    }
}