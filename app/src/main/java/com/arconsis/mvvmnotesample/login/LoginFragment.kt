package com.arconsis.mvvmnotesample.login

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arconsis.mvvmnotesample.R
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.data.getLocalUser
import com.arconsis.mvvmnotesample.data.isLocalUserPresent
import com.arconsis.mvvmnotesample.data.saveLocalUser
import com.arconsis.mvvmnotesample.databinding.LoginFragmentBinding
import com.arconsis.mvvmnotesample.notes.NotesActivity
import com.arconsis.mvvmnotesample.util.Herder
import com.arconsis.mvvmnotesample.util.ProgressDialogFragment
import com.arconsis.mvvmnotesample.util.toast

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginFragment : Fragment(), LoginViewModel.LoginActions {

    private val loginViewModel by Herder(null) { LoginViewModel(LoginService()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (context.isLocalUserPresent()) {
            activity.finish()
            NotesActivity.start(activity, context.getLocalUser())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("XXX", "Fragment id = " + activity.taskId)

        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.vm = loginViewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (!loginViewModel.processing) {
            withProgress {}
        }

        loginViewModel.savedUser?.let { user ->
            onLoginSuccessful(user)
        }

        loginViewModel.loginActions = this
    }

    override fun onStop() {
        loginViewModel.loginActions = null
        super.onStop()
    }

    override fun onLoginSuccessful(user: User) {
        withProgress {
            context.saveLocalUser(user)
            activity.finish()
            NotesActivity.start(activity, user)
        }
    }

    override fun onLoginFailed() {
        withProgress {
            context.toast("Login failed")
        }
    }

    override fun processing() {
        ProgressDialogFragment.create(getString(R.string.running)).show(fragmentManager, PROGRESS_TAG)
    }

    override fun onDataMissing() {
        context.toast("Please enter username and password")
    }

    private fun withProgress(block: () -> Unit) {
        val fragment = fragmentManager.findFragmentByTag(PROGRESS_TAG)
        if (fragment != null && fragment is DialogFragment) {
            fragment.dismiss()
        }
        block()
    }

    companion object {
        @JvmStatic
        private val PROGRESS_TAG = "progress"

        fun create(): LoginFragment {
            return LoginFragment()
        }
    }
}