package com.developerphil.adbidea.hdc

import com.developerphil.adbidea.hdc.DeviceResult.SuccessfulDeviceResult
import com.developerphil.adbidea.hdc.command.*
import com.developerphil.adbidea.ui.NotificationHelper
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.intellij.openapi.project.Project
import java.util.concurrent.Executors

/**
 * HDC 操作门面类
 * 提供所有 HDC 操作的统一入口
 */
object HdcFacade {
    private val EXECUTOR = Executors.newCachedThreadPool(
        ThreadFactoryBuilder().setNameFormat("HdcIdea-%d").build()
    )

    fun uninstall(project: Project) = executeOnDevice(project, UninstallCommand())

    fun kill(project: Project) = executeOnDevice(project, KillCommand())

    fun grantPermissions(project: Project) = executeOnDevice(project, GrantPermissionsCommand())

    fun revokePermissions(project: Project) = executeOnDevice(project, RevokePermissionsCommand())

    fun revokePermissionsAndRestart(project: Project) = executeOnDevice(project, RevokePermissionsAndRestartCommand())

    fun startDefaultAbility(project: Project) = executeOnDevice(project, StartDefaultAbilityCommand(false))

    fun startDefaultAbilityWithDebugger(project: Project) = executeOnDevice(project, StartDefaultAbilityCommand(true))

    fun restartDefaultAbility(project: Project) = executeOnDevice(project, RestartPackageCommand())

    fun restartDefaultAbilityWithDebugger(project: Project) =
        executeOnDevice(project, CommandList(KillCommand(), StartDefaultAbilityCommand(true)))

    fun clearData(project: Project) = executeOnDevice(project, ClearDataCommand())

    fun clearDataAndRestart(project: Project) = executeOnDevice(project, ClearDataAndRestartCommand())

    fun clearDataAndRestartWithDebugger(project: Project) =
        executeOnDevice(project, ClearDataAndRestartWithDebuggerCommand())

    private fun executeOnDevice(project: Project, command: Command) {
        val objectGraph = project.getService(HdcObjectGraph::class.java)

        when (val result = objectGraph.deviceResultFetcher.fetch()) {
            is SuccessfulDeviceResult -> {
                result.devices.forEach { device ->
                    EXECUTOR.submit {
                        command.run(
                            CommandContext(
                                project = project,
                                device = device,
                                bundleName = result.bundleName,
                                mainAbility = result.mainAbility,
                                commandExecutor = objectGraph.commandExecutor,
                                coroutineScope = objectGraph.projectScope
                            )
                        )
                    }
                }
            }

            is DeviceResult.Cancelled -> Unit
            is DeviceResult.DeviceNotFound, null -> NotificationHelper.error("No HarmonyOS device found")
        }
    }
}
