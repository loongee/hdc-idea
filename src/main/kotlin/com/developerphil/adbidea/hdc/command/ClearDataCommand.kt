package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.hdc.ShellCommandsFactory
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info

/**
 * 清除应用数据命令
 * 使用 HDC: bm clean -n <bundleName> (-c|-d)
 */
class ClearDataCommand : Command {
    override fun run(context: CommandContext): Boolean = with(context) {
        try {
            if (commandExecutor.isAppInstalled(device.serialNumber, bundleName)) {
                val shellCommand = ShellCommandsFactory.clearAppData(bundleName)
                val result = commandExecutor.executeShellCommand(device.serialNumber, shellCommand)

                if (result.isSuccess) {
                    info(String.format("<b>%s</b> cleared data on %s", bundleName, device.displayName))
                    return true
                } else {
                    error(String.format("Failed to clear data for <b>%s</b> on %s: %s", bundleName, device.displayName, result.errorOutput))
                }
            } else {
                error(String.format("<b>%s</b> is not installed on %s", bundleName, device.displayName))
            }
        } catch (e: Exception) {
            error("Clear data fail... " + e.message)
        }
        return false
    }
}
