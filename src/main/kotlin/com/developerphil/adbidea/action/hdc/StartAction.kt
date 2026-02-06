package com.developerphil.adbidea.action.hdc

import com.developerphil.adbidea.hdc.HdcFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * 启动应用 Action
 */
class StartAction : HdcAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) = HdcFacade.startDefaultAbility(project)
}
