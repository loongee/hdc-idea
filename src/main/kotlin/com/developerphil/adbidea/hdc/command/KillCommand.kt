package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.hdc.ShellCommandsFactory
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info

/**
 * 强制停止应用命令
 * 使用 HDC: aa force-stop <bundleName>
 */
class KillCommand : Command {
    override fun run(context: CommandContext): Boolean = with(context) {
        try {
            if (commandExecutor.isAppInstalled(device.serialNumber, bundleName)) {
                val shellCommand = ShellCommandsFactory.forceStop(bundleName)
                val result = commandExecutor.executeShellCommand(device.serialNumber, shellCommand)

                if (result.isSuccess) {
                    info(String.format("<b>%s</b> forced-stop on %s", bundleName, device.displayName))
                    return true
                } else {
                    error(String.format("Failed to stop <b>%s</b> on %s: %s", bundleName, device.displayName, result.errorOutput))
                }
            } else {
                error(String.format("<b>%s</b> is not installed on %s", bundleName, device.displayName))
            }
        } catch (e: Exception) {
            error("Kill fail... " + e.message)
        }
        return false
    }
}
