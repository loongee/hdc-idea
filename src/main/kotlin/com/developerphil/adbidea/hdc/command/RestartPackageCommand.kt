package com.developerphil.adbidea.hdc.command

/**
 * 重启应用命令
 * 组合 KillCommand 和 StartDefaultAbilityCommand
 */
class RestartPackageCommand : CommandList(KillCommand(), StartDefaultAbilityCommand(false))
