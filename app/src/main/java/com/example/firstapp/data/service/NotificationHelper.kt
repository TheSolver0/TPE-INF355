package com.example.firstapp.data.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showTaskAddedNotification(todoLabel: String) {
        val notification = NotificationCompat.Builder(context, "todo_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Tâche ajoutée !")
            .setContentText("« $todoLabel » a été ajoutée à votre liste")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("« $todoLabel » a été ajoutée à votre liste. Vous pouvez la marquer comme terminée quand c'est fait !"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(generateNotificationId(), notification)
    }

    fun showTaskCompletedNotification(todoLabel: String) {
        val notification = NotificationCompat.Builder(context, "todo_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Tâche terminée !")
            .setContentText("« $todoLabel » a été marquée comme terminée")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Félicitations ! Vous avez terminé « $todoLabel ». Continuez comme ça !"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(generateNotificationId(), notification)
    }

    fun showTaskDeletedNotification(todoLabel: String) {
        val notification = NotificationCompat.Builder(context, "todo_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Tâche supprimée")
            .setContentText("« $todoLabel » a été supprimée")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(generateNotificationId(), notification)
    }

    private fun generateNotificationId(): Int {
        return System.currentTimeMillis().toInt()
    }
}