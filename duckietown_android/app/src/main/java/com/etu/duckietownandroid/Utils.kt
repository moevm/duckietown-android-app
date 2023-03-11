package com.etu.duckietownandroid

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

private val client = OkHttpClient()
fun safeNavigation(nav: NavController, @IdRes id: Int, args: Bundle? = null){
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