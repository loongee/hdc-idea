package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.hdc.ShellCommandsFactory
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info

/**
 * 撤销权限命令
 * 使用 HDC: bm revoke -n <bundleName> -p <permission>
 */
class RevokePermissionsCommand : Command {
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

                // 解析已授予的权限
                val grantedPermissions = parseGrantedPermissions(dumpResult.output)

                if (grantedPermissions.isEmpty()) {
                    info("No granted permissions found for <b>$bundleName</b>")
                    return true
                }

                // 撤销每个权限
                var allRevoked = true
                for (permission in grantedPermissions) {
                    try {
                        val revokeCommand = ShellCommandsFactory.revokePermission(bundleName, permission)
                        val result = commandExecutor.executeShellCommand(device.serialNumber, revokeCommand)

                        if (result.isSuccess) {
                            info(String.format("Permission <b>%s</b> revoked on %s", permission, device.displayName))
                        } else {
                            error(String.format("Revoking %s failed on %s: %s", permission, device.displayName, result.errorOutput))
                            allRevoked = false
                        }
                    } catch (e: Exception) {
                        error(String.format("Revoking %s failed on %s: %s", permission, device.displayName, e.message))
                        allRevoked = false
                    }
                }
                return allRevoked
            } else {
                error(String.format("<b>%s</b> is not installed on %s", bundleName, device.displayName))
            }
        } catch (e: Exception) {
            error("Revoking permissions fail... " + e.message)
        }
        return false
    }

    /**
     * 从 bm dump 输出解析已授予的权限
     */
    private fun parseGrantedPermissions(dumpOutput: String): List<String> {
        val permissions = mutableListOf<String>()
        val lines = dumpOutput.lines()

        for (line in lines) {
            val trimmedLine = line.trim()

            // 查找已授予的权限 (state=granted 或类似标记)
            if (trimmedLine.contains("ohos.permission", ignoreCase = true) &&
                (trimmedLine.contains("granted", ignoreCase = true) ||
                        trimmedLine.contains("state: true", ignoreCase = true))) {

                val permissionPattern = Regex("(ohos\\.permission\\.[A-Z_]+)")
                permissionPattern.find(trimmedLine)?.let {
                    permissions.add(it.value)
                }
            }
        }

        return permissions.distinct()
    }
}
