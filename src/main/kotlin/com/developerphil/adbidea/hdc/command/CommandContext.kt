package com.developerphil.adbidea.hdc.command

import com.developerphil.adbidea.hdc.HdcCommandExecutor
import com.developerphil.adbidea.hdc.HdcDevice
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

/**
 * HDC 命令上下文
 * 用于在执行命令时传递必要的信息
 */
data class CommandContext(
    val project: Project,
    val device: HdcDevice,
    val bundleName: String,
    val mainAbility: String?,
    val commandExecutor: HdcCommandExecutor,
    val coroutineScope: CoroutineScope
)
