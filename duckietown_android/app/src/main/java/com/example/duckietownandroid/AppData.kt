package com.example.duckietownandroid

object AppData {
    val autobots = MutableList(27){index ->
        DeviceItem(
            "Autobot ${index + 1}",
            index % 3 != 0,
            when(index % 3){
                0 -> "Offline"
                else -> "Online"
            }
        )
    }
}