package com.example.duckietownandroid

data class DeviceItem(var name: String,
                      var is_online: Boolean = false,
                      var shortStatus: String = "",
                      var fullStatus: MutableMap<String, String> = HashMap())
