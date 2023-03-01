package com.etu.duckietownandroid

data class DeviceItem(var number: Int,
                      var prefix: String,
                      var is_online: Boolean = false,
                      var shortStatus: String = "",
                      var fullStatus: MutableMap<String, String> = HashMap()){
    val name = "$prefix $number"
}
