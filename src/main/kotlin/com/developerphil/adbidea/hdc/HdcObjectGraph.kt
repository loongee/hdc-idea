package com.developerphil.adbidea.hdc

import com.developerphil.adbidea.preference.ProjectPreferences
import com.developerphil.adbidea.preference.accessor.PreferenceAccessorImpl
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

/**
 * HDC 服务定位器
 * 提供所有 HDC 相关服务的依赖注入
 */
@Service(Service.Level.PROJECT)
class HdcObjectGraph(private val project: Project, coroutineScope: CoroutineScope) {

    val deviceResultFetcher by lazy { DeviceResultFetcher(project, useSameDevicesHelper, bridge) }
    val projectPreferences: ProjectPreferences by lazy { ProjectPreferences(projectPreferenceAccessor) }
    val projectScope: CoroutineScope = coroutineScope
    val commandExecutor by lazy { HdcCommandExecutor(project) }

    private val useSameDevicesHelper by lazy { UseSameDevicesHelper(projectPreferences, bridge) }
    private val projectPreferenceAccessor by lazy { PreferenceAccessorImpl(PropertiesComponent.getInstance(project)) }
    private val bridge by lazy { HdcBridgeImpl(project) }
}
