package com.etu.duckietownandroid

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
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

fun safeNavigation(nav: NavController, @IdRes id: Int, args: Bundle? = null) {
    nav.currentDestination?.getAction(id)?.run { nav.navigate(id, args) }
}

private val client = OkHttpClient()
private const val NUMBER_OF_AUTOBOTS = 13
private const val NUMBER_OF_WATCHTOWERS = 27
private const val NUMBER_OF_CAMERAS = 6

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

fun fetchAutobot(index: Int): DeviceItem? {
    val request = Request.Builder()
        .url("http://autolab.moevm.info/dbp/autobot${String.format("%02d", index)}/health")
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
    val watchtowerURL =
        "http://autolab.moevm.info/dbp/watchtower${String.format("%02d", index)}/health"
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
    val cameraURL = "http://autolab.moevm.info/camera_${index}/live.mjpg"
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

