package com.etu.duckietownandroid

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class AutobotInfo(
    val temperature: Float,
    val battery: DeviceStatusBatteryInfo,
    val cpu: DeviceStatusCPUInfo,
    val disk: DeviceStatusDiskInfo,
    val swap: DeviceStatusSwapInfo,
    val memory: DeviceStatusMemoryInfo
)

private val client = OkHttpClient()
private const val NUMBER_OF_AUTOBOTS = 13
private const val NUMBER_OF_WATCHTOWERS = 27
private const val NUMBER_OF_CAMERAS = 6

class LabRequests(private val context: Context) {

    fun fetchDevices(deviceType: String): MutableList<DeviceItem> {
        val deviceList = mutableListOf<DeviceItem>()

        when (deviceType) {
            "autobots" -> {
                for (i in 1..NUMBER_OF_AUTOBOTS) {
                    fetchAutobot(i)?.let { deviceList.add(it) }
                }
            }
            "watchtowers" -> {
                for (i in 1..NUMBER_OF_WATCHTOWERS) {
                    fetchWatchtower(i)?.let { deviceList.add(it) }
                }
            }
            "cameras" -> {
                for (i in 1..NUMBER_OF_CAMERAS) {
                    fetchCamera(i)?.let { deviceList.add(it) }
                }
            }
        }
        return deviceList
    }

    fun fetchAutobot(index: Int): DeviceItem? {
        val autobotUrl = getAutobotUrl(index) ?: return null
        val request = Request.Builder()
            .url(autobotUrl)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return DeviceItem(
                        index,
                        "Autobot",
                        false,
                        "Offline",
                        mutableMapOf()
                    )
                } else {
                    val gson = GsonBuilder().create()
                    val data = gson.fromJson(response?.body()?.string(), AutobotInfo::class.java)
                    return DeviceItem(
                        index,
                        "Autobot",
                        true,
                        "Online",
                        mutableMapOf(
                            StatusKeys.TEMPERATURE to DeviceStatusTemperature(data.temperature),
                            StatusKeys.CPU to data.cpu,
                            StatusKeys.BATTERY to data.battery,
                            StatusKeys.MEMORY to data.memory,
                            StatusKeys.SWAP to data.swap,
                            StatusKeys.DISK to data.disk
                        )
                    )
                }
            }
        } catch (err: IOException) {
            return null
        }
    }

    fun fetchWatchtower(index: Int): DeviceItem? {
        val watchtowerURL = getWatchtowerUrl(index) ?: return null
        return when (sendRequest(watchtowerURL)) {
            true -> {
                DeviceItem(
                    index,
                    "Watchtower",
                    true,
                    "Online",
                    mutableMapOf()
                )
            }
            false -> {
                DeviceItem(
                    index,
                    "Watchtower",
                    false,
                    "Offline",
                    mutableMapOf()
                )
            }
            else -> {
                null
            }
        }
    }


    fun fetchCamera(index: Int): DeviceItem? {
        val cameraURL = getCameraUrl(index) ?: return null
        return when (sendRequest(cameraURL)) {
            true -> {
                DeviceItem(
                    index,
                    "Camera",
                    true,
                    "Online",
                    mutableMapOf()
                )
            }
            false -> {
                DeviceItem(
                    index,
                    "Camera",
                    false,
                    "Offline",
                    mutableMapOf()
                )
            }
            else -> {
                null
            }
        }
    }

    fun getCameraUrl(index: Int): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) ?: return null
        var cameraUrl = sharedPreferences.getString("camera_url", "")!!
        val web = sharedPreferences.getBoolean("web", false)
        val labUrl = if (web) {
            sharedPreferences.getString("external_url", "")!!
        } else {
            sharedPreferences.getString("local_url", "")!!
        }

        val strb: StringBuilder = StringBuilder(cameraUrl)
        val lastIndex = strb.lastIndexOf("0")
        strb.replace(lastIndex, 1 + lastIndex, index.toString())
        cameraUrl = strb.toString()

        return labUrl + cameraUrl
    }

    fun getAutobotUrl(index: Int): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) ?: return null
        var autobotUrl = sharedPreferences.getString("bot_url", "")!!
        val web = sharedPreferences.getBoolean("web", false)
        val labUrl = if (web) {
            sharedPreferences.getString("external_url", "")!!
        } else {
            sharedPreferences.getString("local_url", "")!!
        }

        val strb: StringBuilder = StringBuilder(autobotUrl)
        val lastIndex = strb.lastIndexOf("00")
        strb.replace(lastIndex, 2 + lastIndex, String.format("%02d", index))
        autobotUrl = strb.toString()

        return labUrl + autobotUrl
    }

    fun getWatchtowerUrl(index: Int): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) ?: return null
        var watchtowerUrl = sharedPreferences.getString("watchtower_url", "")!!
        val web = sharedPreferences.getBoolean("web", false)
        val labUrl = if (web) {
            sharedPreferences.getString("external_url", "")!!
        } else {
            sharedPreferences.getString("local_url", "")!!
        }

        val strb: StringBuilder = StringBuilder(watchtowerUrl)
        val lastIndex = strb.lastIndexOf("00")
        strb.replace(lastIndex, 2 + lastIndex, String.format("%02d", index))
        watchtowerUrl = strb.toString()

        return labUrl + watchtowerUrl
    }
}

fun safeNavigation(nav: NavController, @IdRes id: Int, args: Bundle? = null) {
    nav.currentDestination?.getAction(id)?.run { nav.navigate(id, args) }
}

fun sendRequest(url: String): Boolean? {
    val request = Request.Builder()
        .url(url)
        .build()

    try {
        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    } catch (err: IOException) {
        return null
    }
}

