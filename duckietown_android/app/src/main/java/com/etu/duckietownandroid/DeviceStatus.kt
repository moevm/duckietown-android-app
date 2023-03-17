package com.etu.duckietownandroid

const val TO_GB = 1024 * 1024 * 1024

interface DeviceStatus{
    fun getStingStatus(): String
}

data class DeviceStatusTemperature(
    val currentTemperature: Float
): DeviceStatus{
    override fun getStingStatus(): String {
        return "Temperature: $currentTemperature°C"
    }
}

data class DeviceStatusBatteryInfo(
    val percentage: Float
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Battery: $percentage%"
    }
}

data class DeviceStatusCPUInfo(
    val percentage: Float
): DeviceStatus{

    override fun getStingStatus(): String {
        return "CPU load: $percentage%"
    }
}

data class DeviceStatusDiskInfo(
    val used: Long,
    val total: Long
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Disk (used/total): ${String.format("%.2f", used.toDouble() / TO_GB)}/${String.format("%.2f", total.toDouble() / TO_GB)} Gb"
    }
}

data class DeviceStatusSwapInfo(
    val used: Long,
    val total: Long
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Swap (used/total): ${String.format("%.2f", used.toDouble() / TO_GB)}/${String.format("%.2f", total.toDouble() / TO_GB)} Gb"
    }
}

data class DeviceStatusMemoryInfo(
    val used: Long,
    val total: Long
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Memory (used/total): ${String.format("%.2f", used.toDouble() / TO_GB)}/${String.format("%.2f", total.toDouble() / TO_GB)} Gb"
    }
}

data class DeviceLastUpdateTime(
    val lastUpdateTime: String
): DeviceStatus{
    override fun getStingStatus(): String {
        return "Last update time: $lastUpdateTime"
    }
}