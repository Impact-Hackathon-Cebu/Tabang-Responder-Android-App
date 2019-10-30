package com.startechup.tabangresponder

import android.app.Dialog
import android.widget.Toast
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_my_dialog.view.*


class MyAlertDialogFragment : DialogFragment() {
    companion object {
        fun instance(message: String) : MyAlertDialogFragment {
            val dialogFragment = MyAlertDialogFragment()
            dialogFragment.arguments = Bundle().also {
                it.putString("message", message)
            }

            return dialogFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val message = arguments?.getString("message") ?: "unknown"

        //here fragment_my_dialog is the UI of Custom Dialog
        val view = inflater.inflate(R.layout.fragment_my_dialog, container, false)

        view.textView_description.text = message

        view.button_go.setOnClickListener {
            acceptCallback?.invoke()
            dialog?.dismiss()
        }

        view.button_cancel.setOnClickListener {
            dialog?.dismiss()
        }

        return view
    }

    private var acceptCallback: (() -> Unit)? = null
    fun setAcceptListener(callback: () -> Unit) {
        acceptCallback = callback
    }


    fun showDialog(manager: FragmentManager) {
        val ft = manager.beginTransaction()
        val prev = manager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        show(manager, "dialog")
    }
}