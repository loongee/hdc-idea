package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.hdc.ShellCommandsFactory
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info

/**
 * 授予权限命令
 * 使用 HDC: bm grant -n <bundleName> -p <permission>
 */
class GrantPermissionsCommand : Command {
    override fun run(context: CommandContext): Boolean = with(context) {
        try {
            if (commandExecutor.isAppInstalled(device.serialNumber, bundleName)) {
                // 获取包的权限信息
                val dumpCommand = ShellCommandsFactory.dumpPackageInfo(bundleName)
                val dumpResult = commandExecutor.executeShellCommand(device.serialNumber, dumpCommand)

                if (!dumpResult.isSuccess) {
                    error("Failed to get package info for <b>$bundleName</b>")
                    return false
                }

                // 解析请求的权限
                val requestedPermissions = parseRequestedPermissions(dumpResult.output)
                info("Found permissions: ${requestedPermissions.joinToString()}")

                // 授予每个权限
                var allGranted = true
                for (permission in requestedPermissions) {
                    try {
                        val grantCommand = ShellCommandsFactory.grantPermission(bundleName, permission)
                        val result = commandExecutor.executeShellCommand(device.serialNumber, grantCommand)

                        if (result.isSuccess) {
                            info(String.format("Permission <b>%s</b> granted on %s", permission, device.displayName))
                        } else {
                            error(String.format("Granting %s failed on %s: %s", permission, device.displayName, result.errorOutput))
                            allGranted = false
                        }
                    } catch (e: Exception) {
                        error(String.format("Granting %s failed on %s: %s", permission, device.displayName, e.message))
                        allGranted = false
                    }
                }
                return allGranted
            } else {
                error(String.format("<b>%s</b> is not installed on %s", bundleName, device.displayName))
            }
        } catch (e: Exception) {
            error("Granting permissions fail... " + e.message)
        }
        return false
    }

    /**
     * 从 bm dump 输出解析请求的权限
     */
    private fun parseRequestedPermissions(dumpOutput: String): List<String> {
        val permissions = mutableListOf<String>()
        val lines = dumpOutput.lines()

        var inPermissionSection = false
        for (line in lines) {
            val trimmedLine = line.trim()

            // 检查是否进入权限部分
            if (trimmedLine.contains("requestPermissions", ignoreCase = true) ||
                trimmedLine.contains("reqPermissionDetails", ignoreCase = true)) {
                inPermissionSection = true
                continue
            }

            // 检查是否离开权限部分
            if (inPermissionSection && (trimmedLine.startsWith("[") || trimmedLine.isEmpty() ||
                        (trimmedLine.contains(":") && !trimmedLine.contains("permission", ignoreCase = true)))) {
                if (!trimmedLine.contains("permission", ignoreCase = true)) {
                    inPermissionSection = false
                }
            }

            // 提取权限名称
            if (inPermissionSection && trimmedLine.contains("ohos.permission", ignoreCase = true)) {
                // 尝试提取权限名称
                val permissionPattern = Regex("(ohos\\.permission\\.[A-Z_]+)")
                permissionPattern.find(trimmedLine)?.let {
                    permissions.add(it.value)
                }
            }
        }

        return permissions.distinct()
    }
}
