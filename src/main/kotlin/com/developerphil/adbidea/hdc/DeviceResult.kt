package com.developerphil.adbidea.hdc

/**
 * 设备获取结果
 */
sealed class DeviceResult {
    /**
     * 成功获取设备结果
     */
    data class SuccessfulDeviceResult(
        val devices: List<HdcDevice>,
        val bundleName: String,
        val mainAbility: String?
    ) : DeviceResult()

    /**
     * 用户取消
     */
    data object Cancelled : DeviceResult()

    /**
     * 未找到设备
     */
    data object DeviceNotFound : DeviceResult()
}
