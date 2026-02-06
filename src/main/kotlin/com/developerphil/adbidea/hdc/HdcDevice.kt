package com.developerphil.adbidea.hdc

/**
 * 表示一个鸿蒙设备
 * 类似于 Android 的 IDevice 接口
 */
data class HdcDevice(
    val serialNumber: String,
    val name: String,
    val state: DeviceState = DeviceState.ONLINE
) {
    enum class DeviceState {
        ONLINE,
        OFFLINE,
        UNAUTHORIZED
    }

    /**
     * 获取设备显示名称
     */
    val displayName: String
        get() = if (name.isNotBlank()) "$name [$serialNumber]" else serialNumber
}
