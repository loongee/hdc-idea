package com.developerphil.adbidea.ui

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType

object NotificationHelper {
    private val loggingGroupName: String
        get() = "HDC Idea (Logging)"

    private val errorsGroupName: String
        get() = "HDC Idea (Errors)"

    private val notificationTitle: String
        get() = "HDC IDEA"

    fun info(message: String) {
        val group = try {
            NotificationGroupManager.getInstance().getNotificationGroup(loggingGroupName)
        } catch (e: Exception) {
            // 如果找不到对应的通知组，使用默认的
            NotificationGroupManager.getInstance().getNotificationGroup("HDC Idea (Logging)")
        }
        sendNotification(message, NotificationType.INFORMATION, group)
    }

    // Function to send an error notification
    fun error(message: String) {
        val group = try {
            NotificationGroupManager.getInstance().getNotificationGroup(errorsGroupName)
        } catch (e: Exception) {
            NotificationGroupManager.getInstance().getNotificationGroup("HDC Idea (Errors)")
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