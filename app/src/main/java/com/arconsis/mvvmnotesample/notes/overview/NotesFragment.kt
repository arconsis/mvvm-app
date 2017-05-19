package com.arconsis.mvvmnotesample.notes.overview

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.arconsis.mvvmnotesample.R
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.User
import com.arconsis.mvvmnotesample.data.removeLocalUser
import com.arconsis.mvvmnotesample.databinding.NotesFragmentBinding
import com.arconsis.mvvmnotesample.login.LoginActivity
import com.arconsis.mvvmnotesample.notes.NoteService
import com.arconsis.mvvmnotesample.notes.create.CreateNoteActivity
import com.arconsis.mvvmnotesample.util.networkChecker
import com.arconsis.mvvmnotesample.util.noteDao
import com.arconsis.mvvmnotesample.util.notesSyncRepository
import com.arconsis.mvvmnotesample.util.toast
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Alexander on 05.05.2017.
 */
class NotesFragment : Fragment(), NotesViewModel.NotesActions {

    private val user by lazy <User> { arguments.getParcelable(ARG_USER) }
    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(ViewModelStores.of(this), NotesViewModelFactory())[NotesViewModel::class.java]
        setHasOptionsMenu(true)
        adapter = NoteAdapter()

        val binding = NotesFragmentBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        binding.noteList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.noteList.adapter = adapter
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.notes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.logout) {
            viewModel.logout()
            context.removeLocalUser()
            activity.finish()
            LoginActivity.start(activity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        viewModel.actions = this
        viewModel.loadNotesForCurrentUser()
    }

    override fun onStop() {
        viewModel.actions = null
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_CREATE_NOTE == requestCode && requestCode == Activity.RESULT_OK) {
            val note = data?.getParcelableExtra<NoteDto>(CreateNoteActivity.RESULT_NOTE)
            if (note != null) {
                adapter.addNewNote(note)
            }
        }
    }

    override fun onNotesAvailable(notes: List<NoteDto>) {
        adapter.updateNotes(notes.toMutableList())
    }

    override fun onCreateNewNote() {
        CreateNoteActivity.startForResult(activity, user, REQUEST_CREATE_NOTE)
    }

    override fun onFailure() {
        toast("A failure occurred")
    }

    private inner class NotesViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
            val noteService = NoteService(context.noteDao, context.networkChecker, AndroidSchedulers.mainThread())
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(user, noteService, context.notesSyncRepository) as T
        }

    }

    companion object {
        private val REQUEST_CREATE_NOTE = 5234
        private val ARG_USER = "user"

        fun create(user: User): NotesFragment {
            val fragment = NotesFragment()
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}