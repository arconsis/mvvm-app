package com.arconsis.mvvmnotesample.create

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arconsis.mvvmnotesample.db.NoteDb
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.databinding.CreateNoteFragmentBinding
import com.arconsis.mvvmnotesample.notes.NoteService
import com.arconsis.mvvmnotesample.util.Herder
import com.arconsis.mvvmnotesample.util.NetworkChecker
import com.arconsis.mvvmnotesample.util.ProgressDialogFragment
import com.arconsis.mvvmnotesample.util.toast
import org.droitateddb.EntityService

class CreateNoteFragment : Fragment(), CreateNoteViewModel.CreateNoteActions {

    private val viewModel by Herder("createdNote") { CreateNoteViewModel(arguments.getParcelable(ARG_USER), NoteService(EntityService(context, NoteDb::class.java), NetworkChecker(context))) }
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
        val binding = CreateNoteFragmentBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.actions = this
    }

    override fun onStop() {
        viewModel.actions = null
        super.onStop()
    }

    override fun onNoteCreated(note: NoteDto) {
        withProgress {
            callback?.onNoteCreated(note)
        }
    }

    override fun onFailure() {
        withProgress {
            context.toast("Note could not be created. Please try again")
        }
    }

    override fun onProcessing() {
        ProgressDialogFragment.create("Creating note...").show(fragmentManager, PROGRESS_TAG)
    }

    override fun onDataMissing() {
        context.toast("Please enter a title and a message")
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