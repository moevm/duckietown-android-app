package com.etu.duckietownandroid

import java.util.*

data class DeviceItem(
    var number: Int,
    var prefix: String,
    var is_online: Boolean = false,
    var shortStatus: String = "",
    var fullStatus: MutableMap<StatusKeys, DeviceStatus> = EnumMap(StatusKeys::class.java)
){
    val name = "$prefix $number"
}
