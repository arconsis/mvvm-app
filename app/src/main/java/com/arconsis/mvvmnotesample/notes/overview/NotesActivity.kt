package com.arconsis.mvvmnotesample.notes.overview

/**
 * Created by Alexander on 04.05.2017.
 */
class NotesActivity : android.support.v7.app.AppCompatActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.arconsis.mvvmnotesample.R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(com.arconsis.mvvmnotesample.R.id.main_container, NotesFragment.create(intent.getParcelableExtra(ARG_USER))).commit()
        }
    }

    companion object {
        private val ARG_USER = "user"

        fun start(activity: android.app.Activity, user: com.arconsis.mvvmnotesample.data.User) {
            val intent = android.content.Intent(activity, NotesActivity::class.java)
            intent.putExtra(com.arconsis.mvvmnotesample.notes.overview.NotesActivity.Companion.ARG_USER, user)
            activity.startActivity(intent)
        }
    }
}