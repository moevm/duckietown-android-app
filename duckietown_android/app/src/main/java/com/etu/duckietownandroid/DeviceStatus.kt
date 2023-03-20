package com.etu.duckietownandroid

const val TO_GB = 1024 * 1024 * 1024

interface DeviceStatus{
    fun getStingStatus(): String

    fun getValueForFilter(): Double
}

data class DeviceStatusTemperature(
    val currentTemperature: Float
): DeviceStatus{
    override fun getStingStatus(): String {
        return "Temperature: $currentTemperature°C"
    }

    override fun getValueForFilter(): Double = currentTemperature.toDouble()
}

data class DeviceStatusBatteryInfo(
    val percentage: Float
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Battery: $percentage%"
    }

    override fun getValueForFilter(): Double = percentage.toDouble()
}

data class DeviceStatusCPUInfo(
    val percentage: Float
): DeviceStatus{

    override fun getStingStatus(): String {
        return "CPU load: $percentage%"
    }

    override fun getValueForFilter(): Double = percentage.toDouble()
}

data class DeviceStatusDiskInfo(
    val used: Long,
    val total: Long
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Disk (used/total): ${String.format("%.2f", used.toDouble() / TO_GB)}/${String.format("%.2f", total.toDouble() / TO_GB)} Gb"
    }

    override fun getValueForFilter(): Double = used.toDouble() / TO_GB
}

data class DeviceStatusSwapInfo(
    val used: Long,
    val total: Long
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Swap (used/total): ${String.format("%.2f", used.toDouble() / TO_GB)}/${String.format("%.2f", total.toDouble() / TO_GB)} Gb"
    }

    override fun getValueForFilter(): Double = used.toDouble() / TO_GB
}

data class DeviceStatusMemoryInfo(
    val used: Long,
    val total: Long
): DeviceStatus{

    override fun getStingStatus(): String {
        return "Memory (used/total): ${String.format("%.2f", used.toDouble() / TO_GB)}/${String.format("%.2f", total.toDouble() / TO_GB)} Gb"
    }

    override fun getValueForFilter(): Double = (total - used).toDouble() / TO_GB
}