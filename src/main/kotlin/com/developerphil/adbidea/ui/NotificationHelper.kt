package com.developerphil.adbidea.ui

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType

object NotificationHelper {

    // 通知模式：ADB 或 HDC
    enum class Mode { ADB, HDC }

    var mode: Mode = Mode.HDC  // 默认使用 HDC 模式

    private val loggingGroupName: String
        get() = if (mode == Mode.HDC) "HDC Idea (Logging)" else "ADB Idea (Logging)"

    private val errorsGroupName: String
        get() = if (mode == Mode.HDC) "HDC Idea (Errors)" else "ADB Idea (Errors)"

    private val notificationTitle: String
        get() = if (mode == Mode.HDC) "HDC IDEA" else "ADB IDEA"

    fun info(message: String) {
        val group = try {
            NotificationGroupManager.getInstance().getNotificationGroup(loggingGroupName)
        } catch (e: Exception) {
            // 如果找不到对应的通知组，使用默认的
            NotificationGroupManager.getInstance().getNotificationGroup("ADB Idea (Logging)")
        }
        sendNotification(message, NotificationType.INFORMATION, group)
    }

    // Function to send an error notification
    fun error(message: String) {
        val group = try {
            NotificationGroupManager.getInstance().getNotificationGroup(errorsGroupName)
        } catch (e: Exception) {
            NotificationGroupManager.getInstance().getNotificationGroup("ADB Idea (Errors)")
        }
        sendNotification(message, NotificationType.ERROR, group)
    }

    // Helper function to create and display a notification
    private fun sendNotification(
        message: String,
        notificationType: NotificationType,
        notificationGroup: NotificationGroup,
    ) {
        // Create the notification without a listener
        val notification = notificationGroup.createNotification(
            notificationTitle,
            escapeString(message),
            notificationType,
        )

        // Display the notification
        notification.notify(null)
    }

    private fun escapeString(string: String) = string.replace(
        "\n".toRegex(),
        "\n<br />"
    )
}