package com.developerphil.adbidea.action.hdc

import com.developerphil.adbidea.hdc.HdcFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * 重启应用 Action
 */
class RestartAction : HdcAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) = HdcFacade.restartDefaultAbility(project)
}
