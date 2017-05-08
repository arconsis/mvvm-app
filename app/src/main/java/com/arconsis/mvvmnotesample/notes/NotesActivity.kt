package com.arconsis.mvvmnotesample.notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arconsis.mvvmnotesample.R
import com.arconsis.mvvmnotesample.data.User

/**
 * Created by Alexander on 04.05.2017.
 */
class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.main_container, NotesFragment.create(intent.getParcelableExtra(ARG_USER))).commit()
        }
    }

    companion object {
        private val ARG_USER = "user"

        fun start(activity: Activity, user: User) {
            val intent = Intent(activity, NotesActivity::class.java)
            intent.putExtra(ARG_USER, user)
            activity.startActivity(intent)
        }
    }
}