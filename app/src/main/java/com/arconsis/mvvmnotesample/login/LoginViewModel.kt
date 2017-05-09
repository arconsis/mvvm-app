package com.arconsis.mvvmnotesample.login

import android.util.Log
import com.arconsis.mvvmnotesample.data.Result
import com.arconsis.mvvmnotesample.data.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.androidobjectherder.HerdedObjectLifecycle

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginViewModel(private val localUser: User?, private val loginService: LoginService) : HerdedObjectLifecycle {
    var username = ""
    var password = ""
    var processing: Boolean = false
    var savedUser: User? = null
    var loginActions: LoginActions? = null
        set(value) {
            field = value
            notifySuccessIfAUserIsPresent()
        }

    val disposable = CompositeDisposable()

    fun doLogin() {
        process {
            loginService.login(username, password)
                    .subscribe(this::onSuccess, this::onError)
        }
    }

    fun doRegister() {
        process {
            loginService.register(username, password)
                    .subscribe(this::onSuccess, this::onError)
        }
    }

    private fun onSuccess(result: Result<User>) {
        processing = false
        if (result.success && result.value != null) {
            savedUser = result.value
            loginActions?.onLoginSuccessful(result.value)
        } else {
            loginActions?.onLoginFailed()
        }
    }

    private fun onError(e: Throwable) {
        Log.e(TAG, "Error", e)
        processing = false
        loginActions?.onLoginFailed()
    }

    override fun unherderd() {
        disposable.dispose()
    }

    private fun process(block: () -> Disposable) {
        if (isDataPresent()) {
            processing = true
            loginActions?.processing()
            disposable.add(block())
        } else {
            loginActions?.onDataMissing()
        }
    }

    private fun isDataPresent(): Boolean = username.isNotEmpty() && password.isNotEmpty()

    private fun notifySuccessIfAUserIsPresent() {
        val saved = savedUser
        if (saved != null) {
            loginActions?.onLoginSuccessful(saved)
        } else if (localUser != null) {
            loginActions?.onLoginSuccessful(localUser)
        }
    }

    interface LoginActions {
        fun onLoginSuccessful(user: User)
        fun onLoginFailed()
        fun processing()
        fun onDataMissing()
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}