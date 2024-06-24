package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.AppConstant.Companion.ACTION_API_CALL
import com.example.myapplication.AppConstant.Companion.ACTION_MORE_INFO
import com.example.myapplication.AppConstant.Companion.CHANNEL_NAME
import com.example.myapplication.AppConstant.Companion.EXTRA_NOTIFICATION_ACTION
import com.example.myapplication.AppConstant.Companion.EXTRA_NOTIFICATION_ID
import com.google.firebase.messaging.FirebaseMessagingService.NOTIFICATION_SERVICE
import java.util.Random

class NotificationHelper {
    fun sendRefillNotification(context: Context, title: String, message: String) {
        val notificationId = Random().nextInt()
        val channelId = context.getString(R.string.default_notification_channel_id)

        val intent1 = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_API_CALL
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val pendingIntent1: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent1,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        val intent2 = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_NOTIFICATION_ACTION, ACTION_MORE_INFO)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        val pendingIntent2: PendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent2,
                PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setAutoCancel(true)
            .addAction(R.drawable.icon1, context.getString(R.string.refill_now), pendingIntent1)
            .addAction(R.drawable.icon2, context.getString(R.string.more_info), pendingIntent2)

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, CHANNEL_NAME, IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, notificationBuilder.build())
    }

    fun showResultNotification(context: Context, message: String) {
        val notificationId = Random().nextInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, FLAG_IMMUTABLE
        )

        val channelId = context.getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Refilled Result")
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, CHANNEL_NAME, IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, notificationBuilder.build())
    }
}