package com.developerphil.adbidea.hdc

import com.intellij.openapi.project.Project
import com.huawei.deveco.hdclib.ohos.hdc.HarmonyDebugConnector

/**
 * HDC 桥接接口
 * 类似于 Android 的 AndroidDebugBridge
 */
interface HdcBridge {
    /**
     * 检查 HDC 是否就绪
     */
    fun isReady(): Boolean

    /**
     * 获取已连接的设备列表
     */
    fun connectedDevices(): List<HdcDevice>

    /**
     * 获取 HDC 路径
     */
    fun getHdcPath(): String?
}

/**
 * HDC 桥接实现类
 */
class HdcBridgeImpl(private val project: Project) : HdcBridge {

    private val commandExecutor = HdcCommandExecutor(project)

    override fun isReady(): Boolean {
        return commandExecutor.getHdcPath() != null
    }

    override fun connectedDevices(): List<HdcDevice> {
        return HarmonyDebugConnector.getHdcConnector().devices.map {
            HdcDevice(it.serialNumber, it.deviceName)
        }
    }

    override fun getHdcPath(): String? {
        return commandExecutor.getHdcPath()
    }
}
