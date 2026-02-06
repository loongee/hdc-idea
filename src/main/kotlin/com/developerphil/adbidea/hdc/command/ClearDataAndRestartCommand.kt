package com.developerphil.adbidea.hdc.command

/**
 * 清除数据并重启命令
 * 组合 ClearDataCommand 和 StartDefaultAbilityCommand
 */
class ClearDataAndRestartCommand : CommandList(ClearDataCommand(), StartDefaultAbilityCommand(false))
