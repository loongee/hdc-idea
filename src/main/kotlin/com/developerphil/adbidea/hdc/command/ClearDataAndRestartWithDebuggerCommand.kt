package com.developerphil.adbidea.hdc.command

/**
 * 清除数据并以调试模式重启命令
 * 组合 ClearDataCommand 和 StartDefaultAbilityCommand(debug=true)
 */
class ClearDataAndRestartWithDebuggerCommand : CommandList(ClearDataCommand(), StartDefaultAbilityCommand(true))
