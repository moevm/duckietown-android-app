package com.etu.duckietownandroid

import android.util.Log
import java.util.EnumMap

class DeviceFilter {
    private val constraints: MutableMap<StatusKeys, Pair<Double?, Double?>> = EnumMap(com.etu.duckietownandroid.StatusKeys::class.java)
    var onlyOnline = false

    private fun checkDevice(device: DeviceItem): Boolean{
        constraints.forEach{ entry ->
            if(entry.value.first != null || entry.value.second != null){
                Log.d("FILTER", "Key: ${entry.key}, values: ${entry.value}")
                val deviceValue = device.fullStatus[entry.key]?.getValueForFilter() ?: return false
                if(entry.value.first != null && entry.value.first!! > deviceValue) return false
                if(entry.value.second != null && entry.value.second!! < deviceValue) return false
            }
        }
        return true
    }

    fun applyFilter(devices: MutableList<DeviceItem>): MutableList<DeviceItem>{
        val filteredDevices = mutableListOf<DeviceItem>()
        for(device in devices){
            if(onlyOnline && !device.is_online) continue
            if(checkDevice(device)) filteredDevices.add(device)
        }
        return  filteredDevices
    }

    fun addConstraint(key: StatusKeys, lower: Double?, upper: Double?){
        constraints[key] = Pair(lower, upper)
    }

    fun removeConstraint(key: StatusKeys){
        constraints.remove(key)
    }

    fun getConstraint(key: StatusKeys): Pair<Double?, Double?>{
        return constraints[key] ?: Pair(null, null)
    }

    fun reset(){
        constraints.clear()
    }
}