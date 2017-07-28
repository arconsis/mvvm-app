package com.arconsis.mvvmnotesample.login

import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arconsis.mvvmnotesample.R
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.data.getLocalUser
import com.arconsis.mvvmnotesample.data.isLocalUserPresent
import com.arconsis.mvvmnotesample.data.saveLocalUser
import com.arconsis.mvvmnotesample.databinding.LoginFragmentBinding
import com.arconsis.mvvmnotesample.login.vm.LoginViewModel
import com.arconsis.mvvmnotesample.login.vm.ProcessingState
import com.arconsis.mvvmnotesample.login.vm.ProcessingStateChangedEvent
import com.arconsis.mvvmnotesample.notes.overview.NotesActivity
import com.arconsis.mvvmnotesample.util.ProgressDialogFragment
import com.arconsis.mvvmnotesample.util.notesSyncRepository
import com.arconsis.mvvmnotesample.util.toast
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginFragment : LifecycleFragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var currentState: ProcessingState? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentState = savedInstanceState?.getSerializable(SAVED_CURRENT_STATE) as ProcessingState?

        loginViewModel = ViewModelProvider(ViewModelStores.of(this), LoginViewModelFactory())[LoginViewModel::class.java]
        loginViewModel.processingState.observe(this, Observer<ProcessingStateChangedEvent<User>>(this::handleProcessingStateChange))
        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.vm = loginViewModel
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(SAVED_CURRENT_STATE, currentState)
    }

    private fun handleProcessingStateChange(stateChangedEvent: ProcessingStateChangedEvent<User>?) {
        val state = stateChangedEvent?.state
        if (state == currentState) {
            return
        }
        currentState = state
        withProgress {
            when (state) {
                ProcessingState.Processing -> ProgressDialogFragment.create(getString(R.string.running)).show(fragmentManager, PROGRESS_TAG)
                ProcessingState.DataMissing -> toast("Please enter username and password")
                ProcessingState.Failed -> toast("Login failed")
                ProcessingState.Login -> onLoginState(stateChangedEvent.data)
                null -> {
                    // do nothing if state is unknown
                }
            }
        }
    }

    private fun onLoginState(user: User?) {
        if (user == null) {
            toast("Login failed")
        } else {
            context.saveLocalUser(user)
            activity.finish()
            NotesActivity.start(activity, user)
        }
    }

    private fun withProgress(block: () -> Unit = {}) {
        val fragment = fragmentManager.findFragmentByTag(PROGRESS_TAG)
        if (fragment != null && fragment is DialogFragment) {
            fragment.dismiss()
        }
        block()
    }

    private inner class LoginViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val local: User? = if (context.isLocalUserPresent()) {
                context.getLocalUser()
            } else {
                null
            }
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(local, LoginService(AndroidSchedulers.mainThread()), context.notesSyncRepository) as T
        }
    }

    companion object {
        @JvmStatic
        private val PROGRESS_TAG = "progress"
        @JvmStatic
        private val SAVED_CURRENT_STATE = "currentState"

        fun create(): LoginFragment {
            return LoginFragment()
        }
    }
}