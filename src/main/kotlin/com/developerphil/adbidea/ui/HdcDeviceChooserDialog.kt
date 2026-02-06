package com.developerphil.adbidea.ui

import com.developerphil.adbidea.hdc.HdcDevice
import com.developerphil.adbidea.hdc.HdcObjectGraph
import com.developerphil.adbidea.preference.ProjectPreferences
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

/**
 * HDC 设备选择对话框
 * 用于在多个鸿蒙设备之间进行选择
 */
class HdcDeviceChooserDialog(
    private val project: Project,
    private val devices: List<HdcDevice>
) : DialogWrapper(project, true) {

    private val projectPreferences: ProjectPreferences =
        project.getService(HdcObjectGraph::class.java).projectPreferences

    private val rootPanel = JPanel(BorderLayout())
    private val deviceList: JBList<HdcDevice>
    private val useSameDeviceCheckBox = JCheckBox("Use same device(s) for future commands")

    val selectedDevices: List<HdcDevice>
        get() = deviceList.selectedValuesList

    init {
        title = "Choose HarmonyOS Device"
        okAction.isEnabled = false

        // 创建设备列表
        deviceList = JBList(devices)
        deviceList.cellRenderer = HdcDeviceCellRenderer()
        deviceList.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

        // 添加选择监听器
        deviceList.addListSelectionListener {
            okAction.isEnabled = deviceList.selectedValuesList.isNotEmpty()
        }

        // 预选之前选择的设备
        val previousSerials = projectPreferences.getSelectedDeviceSerials()
        val indicesToSelect = devices.mapIndexedNotNull { index, device ->
            if (previousSerials.contains(device.serialNumber)) index else null
        }
        if (indicesToSelect.isNotEmpty()) {
            indicesToSelect.forEach { deviceList.addSelectionInterval(it, it) }
        }

        // 构建 UI
        val scrollPane = JBScrollPane(deviceList)
        scrollPane.preferredSize = Dimension(400, 200)

        rootPanel.add(scrollPane, BorderLayout.CENTER)
        rootPanel.add(useSameDeviceCheckBox, BorderLayout.SOUTH)

        init()
    }

    override fun doOKAction() {
        // 保存选择的设备
        projectPreferences.saveSelectedDeviceSerials(
            selectedDevices.map { it.serialNumber }
        )
        super.doOKAction()
    }

    override fun createCenterPanel(): JComponent = rootPanel

    override fun getPreferredFocusedComponent(): JComponent = deviceList

    override fun getDimensionServiceKey(): String = javaClass.canonicalName

    fun useSameDevices(): Boolean = useSameDeviceCheckBox.isSelected

    /**
     * 设备列表单元格渲染器
     */
    private class HdcDeviceCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): java.awt.Component {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

            if (value is HdcDevice) {
                text = buildString {
                    append(value.serialNumber)
                    if (value.name.isNotBlank()) {
                        append(" - ")
                        append(value.name)
                    }
                    append(" [")
                    append(value.state.name)
                    append("]")
                }
                icon = null // 可以添加设备图标
            }

            return this
        }
    }
}
