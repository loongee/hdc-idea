package com.developerphil.adbidea.action.hdc

import com.intellij.ide.actions.QuickSwitchSchemeAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project

/**
 * HDC 操作快速列表
 */
class QuickListAction : QuickSwitchSchemeAction(), DumbAware {
    override fun fillActions(project: Project?, group: DefaultActionGroup, dataContext: DataContext) {
        if (project == null) {
            return
        }

        addAction("com.developerphil.adbidea.action.hdc.UninstallAction", group)
        addAction("com.developerphil.adbidea.action.hdc.KillAction", group)
        addAction("com.developerphil.adbidea.action.hdc.StartAction", group)
        addAction("com.developerphil.adbidea.action.hdc.RestartAction", group)
        addAction("com.developerphil.adbidea.action.hdc.ClearDataAction", group)
        addAction("com.developerphil.adbidea.action.hdc.ClearDataAndRestartAction", group)
        addAction("com.developerphil.adbidea.action.hdc.RevokePermissionsAction", group)
        group.addSeparator()
        addAction("com.developerphil.adbidea.action.hdc.StartWithDebuggerAction", group)
        addAction("com.developerphil.adbidea.action.hdc.RestartWithDebuggerAction", group)
    }

    private fun addAction(actionId: String, toGroup: DefaultActionGroup) {
        ActionManager.getInstance().getAction(actionId)?.let {
            toGroup.add(it)
        }
    }

    override fun isEnabled() = true
    override fun getPopupTitle(e: AnActionEvent) = "HDC Operations Popup"
}
