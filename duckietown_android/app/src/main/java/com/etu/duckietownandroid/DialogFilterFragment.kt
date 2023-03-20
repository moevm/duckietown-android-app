package com.etu.duckietownandroid

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.RangeSlider

class DialogFilterFragment(
    private val listener: DeviceFilterInterface
): DialogFragment() {

    private lateinit var filter: DeviceFilter
    private var minTemperature: EditText? = null
    private var maxTemperature: EditText? = null
    private var maxMemoryLeft: EditText? = null
    private var maxBatterySlider: RangeSlider? = null
    private var onlyOnline: SwitchCompat? = null
    private var maxBatterySwitch: SwitchCompat? = null

    interface DeviceFilterInterface{
        fun getFilter(): DeviceFilter

        fun applyFilter(newData: MutableList<DeviceItem>? = null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_filter_fragment, null))
                .setTitle("Filter")

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    private fun convertToDouble(string: String?): Double?{
        if(string == null) return null
        if(string.isEmpty()) return null
        return string.toDouble()
    }

    override fun onStart() {
        super.onStart()
        filter = listener.getFilter()
        dialog?.findViewById<Button>(R.id.filter_button_apply)?.setOnClickListener {
            filter.onlyOnline = onlyOnline?.isChecked ?: false
            Log.d("FILTER", "OnlyOnline: ${onlyOnline?.isChecked}")
            val batteryValues = maxBatterySlider?.values
            if(maxBatterySwitch?.isChecked == true){
                filter.addConstraint(StatusKeys.BATTERY, batteryValues?.get(0)?.toDouble(), batteryValues?.get(1)?.toDouble())
            }else{
                filter.removeConstraint(StatusKeys.BATTERY)
            }
            filter.addConstraint(StatusKeys.TEMPERATURE, convertToDouble(minTemperature?.text?.toString()), convertToDouble(maxTemperature?.text?.toString()))
            convertToDouble(maxMemoryLeft?.text?.toString())?.let {
                filter.addConstraint(StatusKeys.MEMORY, null, it)
            }
            listener.applyFilter()
            dialog?.dismiss()
        }
        dialog?.findViewById<Button>(R.id.filter_button_reset)?.setOnClickListener {
            filter.reset()
            listener.applyFilter()
            dialog?.dismiss()
        }
        onlyOnline = dialog?.findViewById<SwitchCompat>(R.id.filter_only_online)
        onlyOnline?.isChecked = filter.onlyOnline
        val curBattery = filter.getConstraint(StatusKeys.BATTERY)
        maxBatterySlider = dialog?.findViewById<RangeSlider>(R.id.filter_max_battery)
        maxBatterySlider?.valueFrom = 0F
        maxBatterySlider?.valueTo = 100F
        maxBatterySlider?.stepSize = 1F
        maxBatterySlider?.values = mutableListOf(curBattery.first?.toFloat() ?: 0F, curBattery.second?.toFloat() ?: 100F)
        maxBatterySwitch = dialog?.findViewById<SwitchCompat>(R.id.filter_max_battery_hint)
        maxBatterySwitch?.isChecked = curBattery.first != null || curBattery.second != null
        maxBatterySlider?.isEnabled = maxBatterySwitch?.isChecked ?: true
        maxBatterySwitch?.setOnCheckedChangeListener {_, checked ->
            maxBatterySlider?.isEnabled = checked
        }
        minTemperature = dialog?.findViewById<EditText>(R.id.filter_min_temperature)
        maxTemperature = dialog?.findViewById<EditText>(R.id.filter_max_temperature)
        maxMemoryLeft = dialog?.findViewById<EditText>(R.id.filter_max_memory_left)
        val curTemperature = filter.getConstraint(StatusKeys.TEMPERATURE)
        minTemperature?.setText(curTemperature.first?.toInt()?.toString() ?: "")
        maxTemperature?.setText(curTemperature.second?.toInt()?.toString() ?: "")
        val curMemoryLeft = filter.getConstraint(StatusKeys.MEMORY)
        maxMemoryLeft?.setText(curMemoryLeft.second?.toString() ?: "")
    }

}