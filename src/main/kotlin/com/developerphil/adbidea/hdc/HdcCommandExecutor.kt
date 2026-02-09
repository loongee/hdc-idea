package com.developerphil.adbidea.hdc

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import com.huawei.deveco.hdclib.ohos.hdc.HarmonyDebugConnector

/**
 * HDC 命令执行器
 * 负责执行 HDC 命令行工具的各种操作
 */
class HdcCommandExecutor(private val project: Project) {

    companion object {
        private val LOG = Logger.getInstance(HdcCommandExecutor::class.java)
        private const val DEFAULT_TIMEOUT_SECONDS = 30L

        // HDC 环境变量名
        private const val HDC_HOME_ENV = "HDC_HOME"
        private const val DEVECO_SDK_HOME_ENV = "DEVECO_SDK_HOME"
        private const val HARMONY_SDK_HOME_ENV = "HarmonyOS_SDK_HOME"
    }

    /**
     * 命令执行结果
     */
    data class CommandResult(
        val exitCode: Int,
        val output: String,
        val errorOutput: String,
        val isSuccess: Boolean = exitCode == 0
    )

    /**
     * 获取 HDC 可执行文件路径
     */
    fun getHdcPath(): String? {
        return HarmonyDebugConnector.getHdcConnector().hdcPath
    }

    /**
     * 执行 HDC 命令
     */
    fun execute(vararg args: String, timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS): CommandResult {
        val hdcPath = getHdcPath()
        if (hdcPath == null) {
            LOG.warn("HDC executable not found")
            return CommandResult(-1, "", "HDC executable not found", false)
        }

        val command = listOf(hdcPath) + args.toList()
        LOG.info("Executing HDC command: ${command.joinToString(" ")}")

        return try {
            val processBuilder = ProcessBuilder(command)
                .redirectErrorStream(false)

            val process = processBuilder.start()

            val output = StringBuilder()
            val errorOutput = StringBuilder()

            // 读取标准输出
            Thread {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    reader.forEachLine { output.appendLine(it) }
                }
            }.apply { start() }.join(timeoutSeconds * 1000)

            // 读取错误输出
            Thread {
                BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                    reader.forEachLine { errorOutput.appendLine(it) }
                }
            }.apply { start() }.join(timeoutSeconds * 1000)

            val completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            if (!completed) {
                process.destroyForcibly()
                return CommandResult(-1, output.toString(), "Command timed out", false)
            }

            CommandResult(
                exitCode = process.exitValue(),
                output = output.toString().trim(),
                errorOutput = errorOutput.toString().trim()
            )
        } catch (e: Exception) {
            LOG.error("Failed to execute HDC command", e)
            CommandResult(-1, "", e.message ?: "Unknown error", false)
        }
    }

    /**
     * 针对特定设备执行命令
     */
    fun executeOnDevice(deviceSerial: String, vararg args: String): CommandResult {
        return execute("-t", deviceSerial, *args)
    }

    /**
     * 执行 shell 命令
     */
    fun executeShellCommand(deviceSerial: String, shellCommand: String): CommandResult {
        return execute("-t", deviceSerial, "shell", shellCommand)
    }

    /**
     * 获取已连接设备列表
     */
    fun listDevices(): List<HdcDevice> {
        val result = execute("list", "targets", "-v")
        if (!result.isSuccess) {
            LOG.warn("Failed to list devices: ${result.errorOutput}")
            return emptyList()
        }

        return parseDeviceList(result.output)
    }

    /**
     * 解析设备列表输出
     * HDC list targets -v 输出格式:
     * serial_number    device_name    state
     */
    private fun parseDeviceList(output: String): List<HdcDevice> {
        if (output.isBlank()) return emptyList()

        val devices = mutableListOf<HdcDevice>()
        val lines = output.lines().filter { it.isNotBlank() }

        for (line in lines) {
            val parts = line.trim().split("\\s+".toRegex())
            if (parts.isNotEmpty()) {
                val serial = parts[0]
                val name = if (parts.size > 1) parts[1] else ""
                val state = when {
                    parts.any { it.contains("offline", ignoreCase = true) } -> HdcDevice.DeviceState.OFFLINE
                    parts.any { it.contains("unauthorized", ignoreCase = true) } -> HdcDevice.DeviceState.UNAUTHORIZED
                    else -> HdcDevice.DeviceState.ONLINE
                }
                devices.add(HdcDevice(serial, name, state))
            }
        }

        return devices.filter { it.state == HdcDevice.DeviceState.ONLINE }
    }

    /**
     * 检查应用是否已安装
     */
    fun isAppInstalled(deviceSerial: String, bundleName: String): Boolean {
        val result = executeShellCommand(deviceSerial, "bm dump -n $bundleName")
        // 如果包存在，bm dump 会返回包信息；如果不存在，会返回错误信息
        return result.isSuccess && !result.output.contains("error", ignoreCase = true)
            && result.output.contains(bundleName)
    }

    /**
     * 卸载应用
     */
    fun uninstallApp(deviceSerial: String, bundleName: String): CommandResult {
        return execute("-t", deviceSerial, "uninstall", bundleName)
    }

    /**
     * 安装应用
     */
    fun installApp(deviceSerial: String, hapPath: String): CommandResult {
        return execute("-t", deviceSerial, "install", hapPath)
    }
}
