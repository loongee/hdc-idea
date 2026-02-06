package com.developerphil.adbidea.action.hdc

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project

/**
 * HDC Action 基类
 */
abstract class HdcAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT)
        if (project != null) {
            actionPerformed(event, project)
        }
    }

    abstract fun actionPerformed(e: AnActionEvent, project: Project)
}
