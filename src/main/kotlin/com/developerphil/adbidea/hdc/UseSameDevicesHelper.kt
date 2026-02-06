package com.developerphil.adbidea.hdc

import com.developerphil.adbidea.preference.ProjectPreferences

/**
 * 记住上次选择的设备助手
 */
class UseSameDevicesHelper(
    private val projectPreferences: ProjectPreferences,
    private val bridge: HdcBridge
) {
    var previouslyConnectedDevices: List<HdcDevice>? = null

    fun getRememberedDevices(): List<HdcDevice> {
        val selectedDeviceSerials = projectPreferences.getSelectedDeviceSerials()
        val currentlyConnectedDevices = bridge.connectedDevices()

        if (currentlyConnectedDevices == previouslyConnectedDevices) {
            val rememberedDevices = currentlyConnectedDevices.filter {
                selectedDeviceSerials.contains(it.serialNumber)
            }
            if (rememberedDevices.size == selectedDeviceSerials.size) {
                return rememberedDevices
            }
        }

        return emptyList()
    }

    fun rememberDevices() {
        previouslyConnectedDevices = bridge.connectedDevices()
    }
}
