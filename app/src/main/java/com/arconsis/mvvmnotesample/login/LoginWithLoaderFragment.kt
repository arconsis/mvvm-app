package com.arconsis.mvvmnotesample.login

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.Loader
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
import com.arconsis.mvvmnotesample.notes.NotesBackgroundSync
import com.arconsis.mvvmnotesample.util.CachingLoaderCallback
import com.arconsis.mvvmnotesample.util.ProgressDialogFragment
import com.arconsis.mvvmnotesample.util.appContext
import com.arconsis.mvvmnotesample.util.toast
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Alexander on 04.05.2017.
 */
class LoginWithLoaderFragment : Fragment(), LoginViewModel.LoginActions {

    private val loaderCallback by lazy {
        LoginVMLoaderCallback(appContext()) {
            val local: User? = if (context.isLocalUserPresent()) {
                context.getLocalUser()
            } else {
                null
            }
            LoginViewModel(local, LoginService(AndroidSchedulers.mainThread()))
        }
    }

    private lateinit var binding: LoginFragmentBinding
    private lateinit var loader: Loader<LoginViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loader = loaderManager.initLoader(42334, null, loaderCallback)
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onLoginSuccessful(user: User) {
        withProgress {
            NotesBackgroundSync.schedule(context)
            context.saveLocalUser(user)
            activity.finish()
            NotesActivity.start(activity, user)
        }
    }

    override fun onStart() {
        super.onStart()
        loader.startLoading()
    }

    override fun onStop() {
        loader.stopLoading()
        super.onStop()
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
            fragment.dismissAllowingStateLoss()
        }
        block()
    }

    private inner class LoginVMLoaderCallback(context: Context, init: () -> LoginViewModel) : CachingLoaderCallback<LoginViewModel>(context, init) {
        override fun onLoad(data: LoginViewModel) {
            binding.vm = data
            if (!data.processing) {
                withProgress {}
            }
            data.loginActions = this@LoginWithLoaderFragment
        }

        override fun onUnload() {
            binding.vm?.loginActions = null
            binding.vm = null
        }
    }

    companion object {
        @JvmStatic
        private val PROGRESS_TAG = "progress"

        fun create(): LoginWithLoaderFragment {
            return LoginWithLoaderFragment()
        }
    }
}