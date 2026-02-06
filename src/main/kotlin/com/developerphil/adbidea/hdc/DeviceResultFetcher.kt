package com.developerphil.adbidea.hdc

import com.developerphil.adbidea.hdc.DeviceResult.*
import com.developerphil.adbidea.ui.HdcDeviceChooserDialog
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.io.File

/**
 * 设备结果获取器
 * 负责获取鸿蒙设备和项目信息
 */
class DeviceResultFetcher(
    private val project: Project,
    private val useSameDevicesHelper: UseSameDevicesHelper,
    private val bridge: HdcBridge
) {

    fun fetch(): DeviceResult? {
        // 获取项目的 Bundle Name
        val bundleName = getBundleNameFromProject()
        if (bundleName == null) {
            NotificationHelper.error("Unable to determine bundle name from project")
            return null
        }

        // 获取主 Ability
        val mainAbility = getMainAbilityFromProject()

        // 检查 HDC 是否就绪
        if (!bridge.isReady()) {
            NotificationHelper.error("HDC not found. Please ensure HarmonyOS SDK is properly configured.")
            return null
        }

        // 检查记住的设备
        val rememberedDevices = useSameDevicesHelper.getRememberedDevices()
        if (rememberedDevices.isNotEmpty()) {
            return SuccessfulDeviceResult(rememberedDevices, bundleName, mainAbility)
        }

        // 获取已连接的设备
        val devices = bridge.connectedDevices()
        return when {
            devices.size == 1 -> SuccessfulDeviceResult(devices, bundleName, mainAbility)
            devices.size > 1 -> showDeviceChooserDialog(devices, bundleName, mainAbility)
            else -> DeviceNotFound
        }
    }

    /**
     * 从项目配置中获取 Bundle Name
     * 尝试从 module.json5 或 config.json 中读取
     */
    private fun getBundleNameFromProject(): String? {
        val basePath = project.basePath ?: return null

        // 尝试多种配置文件位置
        val configPaths = listOf(
            // 新版 Stage 模型
            "$basePath/entry/src/main/module.json5",
            "$basePath/AppScope/app.json5",
            // FA 模型
            "$basePath/entry/src/main/config.json",
            // 其他可能的位置
            "$basePath/app.json5",
            "$basePath/module.json5"
        )

        for (configPath in configPaths) {
            val configFile = File(configPath)
            if (configFile.exists()) {
                val bundleName = parseBundleNameFromConfig(configFile)
                if (bundleName != null) {
                    return bundleName
                }
            }
        }

        // 如果找不到配置文件，使用项目名作为后备
        return project.name.replace(" ", ".").lowercase().let { "com.example.$it" }
    }

    /**
     * 从配置文件解析 Bundle Name
     */
    private fun parseBundleNameFromConfig(configFile: File): String? {
        try {
            val content = configFile.readText()

            // 尝试匹配 bundleName 字段
            val bundleNamePattern = Regex(""""bundleName"\s*:\s*"([^"]+)"""")
            bundleNamePattern.find(content)?.let {
                return it.groupValues[1]
            }

            // 尝试匹配 package 字段 (FA 模型)
            val packagePattern = Regex(""""package"\s*:\s*"([^"]+)"""")
            packagePattern.find(content)?.let {
                return it.groupValues[1]
            }
        } catch (e: Exception) {
            // 忽略解析错误
        }
        return null
    }

    /**
     * 从项目配置中获取主 Ability
     */
    private fun getMainAbilityFromProject(): String? {
        val basePath = project.basePath ?: return null

        val configPaths = listOf(
            "$basePath/entry/src/main/module.json5",
            "$basePath/entry/src/main/config.json"
        )

        for (configPath in configPaths) {
            val configFile = File(configPath)
            if (configFile.exists()) {
                val mainAbility = parseMainAbilityFromConfig(configFile)
                if (mainAbility != null) {
                    return mainAbility
                }
            }
        }

        // 默认的主 Ability 名称
        return "EntryAbility"
    }

    /**
     * 从配置文件解析主 Ability
     */
    private fun parseMainAbilityFromConfig(configFile: File): String? {
        try {
            val content = configFile.readText()

            // Stage 模型: 查找 mainElement
            val mainElementPattern = Regex(""""mainElement"\s*:\s*"([^"]+)"""")
            mainElementPattern.find(content)?.let {
                return it.groupValues[1]
            }

            // FA 模型: 查找 mainAbility
            val mainAbilityPattern = Regex(""""mainAbility"\s*:\s*"([^"]+)"""")
            mainAbilityPattern.find(content)?.let {
                return it.groupValues[1]
            }
        } catch (e: Exception) {
            // 忽略解析错误
        }
        return null
    }

    /**
     * 显示设备选择对话框
     */
    private fun showDeviceChooserDialog(
        devices: List<HdcDevice>,
        bundleName: String,
        mainAbility: String?
    ): DeviceResult {
        val chooser = HdcDeviceChooserDialog(project, devices)
        chooser.show()

        if (chooser.exitCode != DialogWrapper.OK_EXIT_CODE) {
            return Cancelled
        }

        val selectedDevices = chooser.selectedDevices
        if (selectedDevices.isEmpty()) {
            return Cancelled
        }

        if (chooser.useSameDevices()) {
            useSameDevicesHelper.rememberDevices()
        }

        return SuccessfulDeviceResult(selectedDevices, bundleName, mainAbility)
    }
}
