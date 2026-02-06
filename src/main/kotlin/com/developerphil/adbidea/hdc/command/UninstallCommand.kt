package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info

/**
 * 卸载应用命令
 * 使用 HDC: hdc uninstall <bundleName>
 */
class UninstallCommand : Command {
    override fun run(context: CommandContext): Boolean = with(context) {
        try {
            val result = commandExecutor.uninstallApp(device.serialNumber, bundleName)

            if (result.isSuccess) {
                info(String.format("<b>%s</b> uninstalled on %s", bundleName, device.displayName))
                return true
            } else {
                error(String.format("<b>%s</b> uninstall failed on %s: %s", bundleName, device.displayName, result.errorOutput))
            }
        } catch (e: Exception) {
            error("Uninstall fail... " + e.message)
        }
        return false
    }
}
