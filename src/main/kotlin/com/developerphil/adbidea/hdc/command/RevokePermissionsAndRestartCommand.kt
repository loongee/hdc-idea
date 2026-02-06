package com.developerphil.adbidea.hdc.command

/**
 * 撤销权限并重启命令
 * 组合 RevokePermissionsCommand 和 StartDefaultAbilityCommand
 */
class RevokePermissionsAndRestartCommand : CommandList(RevokePermissionsCommand(), StartDefaultAbilityCommand(false))
