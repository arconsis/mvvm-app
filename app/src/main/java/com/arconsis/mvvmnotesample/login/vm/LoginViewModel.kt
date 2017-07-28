package com.arconsis.mvvmnotesample.login.vm

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.login.LoginService
import com.arconsis.mvvmnotesample.notes.sync.NotesSyncRepository
import com.arconsis.mvvmnotesample.util.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginViewModel(localUser: User?, private val loginService: LoginService, private val notesSyncRepository: NotesSyncRepository) : ViewModel() {
    var username = ""
    var password = ""
    val processingState = SingleLiveEvent<ProcessingStateChangedEvent<User>>()
    val disposable = CompositeDisposable()

    init {
        if (localUser != null) {
            processingState.value = ProcessingStateChangedEvent(ProcessingState.Login, localUser)
        }
    }

    fun doLogin() {
        process {
            loginService.login(username, password).subscribe(this::onSuccess, this::onError)
        }
    }

    fun doRegister() {
        process {
            loginService.register(username, password).subscribe(this::onSuccess, this::onError)
        }
    }

    private fun onSuccess(result: Result<User>) {
        if (result.success && result.value != null) {
            processingState.value = ProcessingStateChangedEvent(ProcessingState.Login, result.value)
            notesSyncRepository.schedule()
        } else {
            processingState.value = ProcessingStateChangedEvent(ProcessingState.Failed)
        }
    }

    private fun onError(e: Throwable) {
        Log.e(TAG, "Error", e)
        processingState.value = ProcessingStateChangedEvent(ProcessingState.Failed)
    }

    override fun onCleared() {
        disposable.dispose()
    }

    private fun process(block: () -> Disposable) {
        if (isDataPresent()) {
            processingState.value = ProcessingStateChangedEvent(ProcessingState.Processing)
            disposable.add(block())
        } else {
            processingState.value = ProcessingStateChangedEvent(ProcessingState.DataMissing)
        }
    }

    private fun isDataPresent(): Boolean = username.isNotEmpty() && password.isNotEmpty()

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}

