package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.hdc.command.CommandContext

/**
 * HDC 命令接口
 * 所有 HDC 命令都需要实现此接口
 */
interface Command {
    /**
     * 执行命令
     * @param context 命令上下文
     * @return true 表示命令执行成功
     */
    fun run(context: CommandContext): Boolean
}
