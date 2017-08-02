package com.arconsis.mvvmnotesample.notes.create

import android.arch.lifecycle.*
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.databinding.CreateNoteFragmentBinding
import com.arconsis.mvvmnotesample.notes.NoteService
import com.arconsis.mvvmnotesample.notes.create.vm.CreateNoteViewModel
import com.arconsis.mvvmnotesample.notes.create.vm.CreateState
import com.arconsis.mvvmnotesample.notes.create.vm.CreateStateChangedEvent
import com.arconsis.mvvmnotesample.util.ProgressDialogFragment
import com.arconsis.mvvmnotesample.util.networkChecker
import com.arconsis.mvvmnotesample.util.noteDao
import com.arconsis.mvvmnotesample.util.toast
import io.reactivex.android.schedulers.AndroidSchedulers

class CreateNoteFragment : LifecycleFragment(), Observer<CreateStateChangedEvent<NoteDto>> {
    private lateinit var viewModel: CreateNoteViewModel
    private var callback: CreateNoteCallback? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context !is CreateNoteCallback) {
            throw IllegalStateException("The attaching context has to implement ${CreateNoteCallback::class.java.canonicalName}")
        }
        callback = context
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(ViewModelStores.of(this), CreateNoteViewModelFactory())[CreateNoteViewModel::class.java]
        viewModel.stateChangeEvent.observe(this, this)
        val binding = CreateNoteFragmentBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        return binding.root
    }

    override fun onChanged(stateChangedEvent: CreateStateChangedEvent<NoteDto>?) {
        withProgress {
            when (stateChangedEvent?.state) {
                CreateState.Failure -> toast("Note could not be created. Please try again")
                CreateState.Processing -> ProgressDialogFragment.create("Creating note...").show(fragmentManager, PROGRESS_TAG)
                CreateState.DataMissing -> toast("Please enter a title and a message")
                CreateState.Created -> handleCreated(stateChangedEvent.data)
                else -> {
                    // if null do nothing
                }
            }
        }
    }

    private fun handleCreated(note: NoteDto?) {
        if (note == null) {
            toast("Note could not be created. Please try again")
        } else {
            callback?.onNoteCreated(note)
        }
    }

    private fun withProgress(block: () -> Unit) {
        val fragment = fragmentManager.findFragmentByTag(PROGRESS_TAG)
        if (fragment != null && fragment is DialogFragment) {
            fragment.dismiss()
        }
        block()
    }

    interface CreateNoteCallback {
        fun onNoteCreated(note: NoteDto)
    }

    private inner class CreateNoteViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val noteService = NoteService(context.noteDao, context.networkChecker, AndroidSchedulers.mainThread())
            @Suppress("UNCHECKED_CAST")
            return CreateNoteViewModel(arguments.getParcelable(ARG_USER), noteService) as T
        }
    }

    companion object {
        private val ARG_USER = "user"
        private val PROGRESS_TAG = "creating"

        fun create(user: User): CreateNoteFragment {
            val fragment = CreateNoteFragment()
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}