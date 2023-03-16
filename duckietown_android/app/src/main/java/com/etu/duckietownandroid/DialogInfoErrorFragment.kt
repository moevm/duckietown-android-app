package com.etu.duckietownandroid

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.transition.Visibility

class DialogInfoErrorFragment(private val text: String,
                              private val title: String? = null,
                              @DrawableRes private val image: Int? = null): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_info_error_fragment, null))
            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            title?.let { builder.setTitle(title) }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<TextView>(R.id.dialog_info_error_text)?.text = text
        if(image != null){
            dialog?.findViewById<ImageView>(R.id.dialog_info_error_image)?.apply {
                setImageResource(image)
                visibility = VISIBLE
            }
        }
    }
}