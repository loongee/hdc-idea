package com.developerphil.adbidea.action.hdc

import com.developerphil.adbidea.hdc.HdcFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * 清除应用数据 Action
 */
class ClearDataAction : HdcAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) = HdcFacade.clearData(project)
}
