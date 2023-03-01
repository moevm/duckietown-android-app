package com.example.duckietownandroid

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.RangeSlider

class DialogFilterFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_filter_fragment, null))
                .setTitle("Filter")

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<Button>(R.id.filter_button_apply)?.setOnClickListener {
            dialog?.dismiss()
        }
        val batterySeek = dialog?.findViewById<RangeSlider>(R.id.filter_max_battery)
        batterySeek?.valueFrom = 0F
        batterySeek?.valueTo = 100F
        batterySeek?.stepSize = 1F
        dialog?.findViewById<EditText>(R.id.filter_min_temperature)?.setText("0")
        dialog?.findViewById<EditText>(R.id.filter_max_temperature)?.setText("100")
        dialog?.findViewById<EditText>(R.id.filter_min_memory_used)?.setText("0.0")
    }

}