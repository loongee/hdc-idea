package com.developerphil.adbidea.action.hdc

import com.developerphil.adbidea.hdc.HdcFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * 撤销权限并重启 Action
 */
class RevokePermissionsAndRestartAction : HdcAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) = HdcFacade.revokePermissionsAndRestart(project)
}
