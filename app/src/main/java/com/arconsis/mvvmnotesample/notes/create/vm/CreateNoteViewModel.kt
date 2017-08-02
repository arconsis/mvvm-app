package com.arconsis.mvvmnotesample.notes.create.vm

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.notes.NoteService
import com.arconsis.mvvmnotesample.util.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Alexander on 08.05.2017.
 */
class CreateNoteViewModel(private val user: User, private val noteService: NoteService) : ViewModel() {
    var title = ""
    var message = ""
    val stateChangeEvent = SingleLiveEvent<CreateStateChangedEvent<NoteDto>>()
    private val disposables = CompositeDisposable()

    fun onAddImage() {
        // TODO implement me
    }

    fun onCreateNote() {
        process {
            noteService.createNote(title, message, user).subscribe(this::onNoteCreated, this::onError, {}, this::onSubscribe)
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
            stateChangeEvent.value = CreateStateChangedEvent(CreateState.Created, result.value)
        } else {
            stateChangeEvent.value = CreateStateChangedEvent(CreateState.Failure)
        }
    }

    private fun process(block: () -> Unit) {
        if (isDataPresent()) {
            stateChangeEvent.value = CreateStateChangedEvent(CreateState.Processing)
            block()
        } else {
            stateChangeEvent.value = CreateStateChangedEvent(CreateState.DataMissing)
        }
    }

    private fun isDataPresent(): Boolean = title.isNotEmpty() && message.isNotEmpty()

    companion object {
        private val TAG = CreateNoteViewModel::class.java.simpleName
    }
}