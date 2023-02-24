package com.example.duckietownandroid

object AppData {
    val autobots = MutableList(13){index ->
        DeviceItem(
            index + 1,
            "Autobot",
            index % 3 != 0,
            when(index % 3){
                0 -> "Offline"
                else -> "Online"
            }
        )
    }

    val watchtowers = MutableList(27){index ->
        DeviceItem(
            index + 1,
            "Watchtower",
            index % 2 != 0,
            when(index % 6){
                0 -> "Offline"
                else -> "Online"
            }
        )
    }

    val cameras = MutableList(6){index ->
        DeviceItem(
            index + 1,
            "Camera",
            index % 5 != 0,
            when(index % 5){
                0 -> "Offline"
                else -> "Online"
            }
        )
    }
}