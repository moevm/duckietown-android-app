package com.etu.duckietownandroid

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController

fun safeNavigation(nav: NavController, @IdRes id: Int, args: Bundle? = null){
    nav.currentDestination?.getAction(id)?.run { nav.navigate(id, args) }
}