package com.example.weathersphere.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.weathersphere.ui.activity.MainActivity
import com.example.weathersphere.R
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.work.WeatherWorker

object NotificationManager {

    private var channelIsCreated = false

    fun createNotificationChannel(context: Context) {
        if (!channelIsCreated) {
            val name = Constants.CHANNEL_NAME
            val descriptionText = Constants.CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val myChannel = NotificationChannel(Constants.CHANNEL_ID, name, importance)
            myChannel.description = descriptionText
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(myChannel)
            channelIsCreated = true
        }
    }

    fun createNotification(context: Context, description: String, zoneName: String): NotificationCompat.Builder {
        val pendingIntent = createPendingIntent(context)
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_notify)
        builder.setContentTitle(context.getString(R.string.weather))
        builder.setContentText("$description in $zoneName")
        builder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("$description in $zoneName")
        )
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        return builder
    }

    fun buildNotification(context: Context, weather: WeatherResponse): Notification {
        val notificationContent = """"Good Morning
            |Temperature Today : ${weather.daily[0].temp}
            |Humidity  ${weather.current.humidity}
            |Alert: ${weather.alerts?.get(0)?.description}
        """.trimMargin()

        val notificationBuilder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createPendingIntent(context))
            .setAutoCancel(true)

        return notificationBuilder.build()
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}