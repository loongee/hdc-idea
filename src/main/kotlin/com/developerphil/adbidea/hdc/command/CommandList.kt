package com.developerphil.adbidea.hdc.command

/**
 * 命令列表
 * 用于组合多个命令按顺序执行
 */
open class CommandList(vararg commands: Command) : Command {

    private val commands = listOf(*commands)

    override fun run(context: CommandContext): Boolean {
        for (command in commands) {
            if (!command.run(context)) {
                return false
            }
        }
        return true
    }
}
