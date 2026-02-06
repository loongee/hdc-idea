package com.developerphil.adbidea.action.hdc

import com.developerphil.adbidea.hdc.HdcFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * 卸载应用 Action
 */
class UninstallAction : HdcAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) = HdcFacade.uninstall(project)
}
