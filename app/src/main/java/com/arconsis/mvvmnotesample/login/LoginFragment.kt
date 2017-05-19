package com.arconsis.mvvmnotesample.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arconsis.mvvmnotesample.R
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.data.getLocalUser
import com.arconsis.mvvmnotesample.data.isLocalUserPresent
import com.arconsis.mvvmnotesample.data.saveLocalUser
import com.arconsis.mvvmnotesample.databinding.LoginFragmentBinding
import com.arconsis.mvvmnotesample.notes.overview.NotesActivity
import com.arconsis.mvvmnotesample.notes.sync.NotesSyncService
import com.arconsis.mvvmnotesample.util.ProgressDialogFragment
import com.arconsis.mvvmnotesample.util.appContext
import com.arconsis.mvvmnotesample.util.toast
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginFragment : Fragment(), LoginViewModel.LoginActions {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loginViewModel = ViewModelProvider(ViewModelStores.of(this), LoginViewModelFactory()).get(LoginViewModel::class.java)

        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.vm = loginViewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (!loginViewModel.processing) {
            withProgress {}
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
            toast("Login failed")
        }
    }

    override fun processing() {
        ProgressDialogFragment.create(getString(R.string.running)).show(fragmentManager, PROGRESS_TAG)
    }

    override fun onDataMissing() {
        toast("Please enter username and password")
    }

    private fun withProgress(block: () -> Unit) {
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
            return LoginViewModel(local, LoginService(AndroidSchedulers.mainThread()), NotesSyncService(appContext())) as T
        }
    }

    companion object {
        @JvmStatic
        private val PROGRESS_TAG = "progress"

        fun create(): LoginFragment {
            return LoginFragment()
        }
    }
}