package com.etu.duckietownandroid

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


fun safeNavigation(nav: NavController, @IdRes id: Int, args: Bundle? = null) {
    nav.currentDestination?.getAction(id)?.run { nav.navigate(id, args) }
}

private val client = OkHttpClient()
private const val NUMBER_OF_AUTOBOTS = 13

fun fetchDevices(deviceType: String): MutableList<DeviceItem> {
    val deviceList = mutableListOf<DeviceItem>()

    when (deviceType) {
        "autobots" -> {
            for (i in 0 until NUMBER_OF_AUTOBOTS) {
                fetchAutobot(i)?.let { deviceList.add(it) }
            }
        }
        "watchtowers" -> {
            // TODO: remove debug for demonstrating update
            for (i in 0 until AppData.watchtowers.size) {
                AppData.watchtowers[i].is_online = !AppData.watchtowers[i].is_online
                if (AppData.watchtowers[i].shortStatus == "Offline")
                {
                    AppData.watchtowers[i].shortStatus = "Online"
                }
                else
                {
                    AppData.watchtowers[i].shortStatus = "Offline"
                }
            }

            for (i in 0..26) {
                fetchWatchtower(i)?.let { deviceList.add(it) }
            }
        }
        "cameras" -> {
            // TODO: remove debug for demonstrating update
            for (i in 0 until AppData.cameras.size) {
                AppData.cameras[i].is_online = !AppData.cameras[i].is_online
                if (AppData.cameras[i].shortStatus == "Offline")
                {
                    AppData.cameras[i].shortStatus = "Online"
                }
                else
                {
                    AppData.cameras[i].shortStatus = "Offline"
                }
            }
            for (i in 0..5) {
                fetchCamera(i)?.let { deviceList.add(it) }
            }
        }
    }
    return deviceList
}

fun fetchAutobot(index: Int): DeviceItem? {
    val request = Request.Builder()
        .url("http://autolab.moevm.info/dbp/autobot${String.format("%02d", index + 1)}/health")
        .get()
        .build()

    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return DeviceItem(
                    index + 1,
                    "Autobot",
                    false,
                    "Offline",
                    mutableMapOf()
                )
            } else {
                return DeviceItem(
                    index + 1,
                    "Autobot",
                    true,
                    "Online",
                    mutableMapOf()
                )
            }
        }
    } catch (err: IOException) {
        return null
    }
}

fun fetchWatchtower(index: Int): DeviceItem? {
    // TODO: add request for watchtower
    return AppData.watchtowers[index]
}


fun fetchCamera(index: Int): DeviceItem? {
    // TODO: add request for camera
    return AppData.cameras[index]
}

