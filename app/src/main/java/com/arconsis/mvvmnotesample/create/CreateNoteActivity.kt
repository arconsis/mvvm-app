package com.arconsis.mvvmnotesample.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arconsis.mvvmnotesample.R
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.data.User

/**
 * Created by Alexander on 08.05.2017.
 */
class CreateNoteActivity : AppCompatActivity(), CreateNoteFragment.CreateNoteCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_note_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.create_note_container, CreateNoteFragment.create(intent.getParcelableExtra(ARG_USER))).commit()
        }
    }

    override fun onNoteCreated(note: NoteDto) {
        val intent = Intent()
        intent.putExtra(RESULT_NOTE, note)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        val RESULT_NOTE = "note"

        private val ARG_USER = "user"

        fun startForResult(activity: Activity, user: User, requestCode: Int) {
            val intent = Intent(activity, CreateNoteActivity::class.java)
            intent.putExtra(ARG_USER, user)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}