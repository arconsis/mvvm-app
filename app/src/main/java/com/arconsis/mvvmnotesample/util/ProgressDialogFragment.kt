package com.arconsis.mvvmnotesample.util

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

/**
 * Created by Alexander on 04.05.2017.
 */
class ProgressDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = ProgressDialog(context)
        dialog.isIndeterminate = true
        dialog.setMessage(arguments.getString(MSG))
        dialog.setCancelable(false)
        return dialog
    }

    companion object {
        @JvmStatic
        private val MSG = "msg"

        fun create(msg: String): ProgressDialogFragment {
            val dialogFragment = ProgressDialogFragment()
            val bundle = Bundle()
            bundle.putString(MSG, msg)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }
}