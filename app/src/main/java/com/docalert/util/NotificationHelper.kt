package com.docalert.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.docalert.MainActivity
import com.docalert.R

object NotificationHelper {

    private const val CHANNEL_ID = "docalert_channel"
    private const val CHANNEL_NAME = "DocAlert Notificaciones"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de vencimiento de documentos"
            enableLights(true)
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun showExpiryNotification(
        context: Context,
        documentId: Long,
        documentName: String,
        daysUntilExpiry: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("document_id", documentId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            documentId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when {
            daysUntilExpiry <= 0 -> "Documento vencido"
            daysUntilExpiry == 1 -> "Documento vence mañana"
            else -> "Documento vence en $daysUntilExpiry días"
        }

        val message = when {
            daysUntilExpiry <= 0 -> "$documentName ha vencido. Renueva lo antes posible."
            daysUntilExpiry <= 7 -> "$documentName vence en $daysUntilExpiry días. No olvides renovarlo."
            else -> "$documentName vence el ${DateUtils.formatDisplayDate(System.currentTimeMillis() + daysUntilExpiry * 24 * 60 * 60 * 1000L)}. Tienes tiempo suficiente."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(documentId.toInt(), notification)
    }

    fun cancelNotification(context: Context, documentId: Long) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(documentId.toInt())
    }
}
