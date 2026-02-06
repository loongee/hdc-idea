package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.hdc.ShellCommandsFactory
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info

/**
 * 启动默认 Ability 命令
 * 使用 HDC: aa start [-D] -b <bundleName> -a <abilityName>
 */
class StartDefaultAbilityCommand(private val withDebugger: Boolean) : Command {
    override fun run(context: CommandContext): Boolean = with(context) {
        try {
            val abilityName = mainAbility
            if (abilityName.isNullOrBlank()) {
                error("Unable to determine main ability for <b>$bundleName</b>")
                return false
            }

            val shellCommand = ShellCommandsFactory.startAbility(bundleName, abilityName, withDebugger)
            val result = commandExecutor.executeShellCommand(device.serialNumber, shellCommand)

            if (result.isSuccess && !result.output.contains("error", ignoreCase = true)) {
                info(String.format("<b>%s</b> started on %s", bundleName, device.displayName))

                // TODO: 如果需要附加调试器，需要集成 DevEco 的调试功能
                if (withDebugger) {
                    info("Debugger attachment for HarmonyOS is not yet implemented")
                }
                return true
            } else {
                error(
                    String.format(
                        "<b>%s</b> could not be started on %s.\n\n<b>HDC Output:</b>\n%s",
                        bundleName,
                        device.displayName,
                        result.output.ifBlank { result.errorOutput }
                    )
                )
            }
        } catch (e: Exception) {
            error("Start fail... " + e.message)
        }
        return false
    }
}
