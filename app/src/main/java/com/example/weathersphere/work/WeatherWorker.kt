package com.example.weathersphere.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathersphere.R
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.example.weathersphere.model.repository.WeatherRepository
import com.example.weathersphere.model.repository.WeatherRepositoryImpl
import com.example.weathersphere.ui.activity.MainActivity
import com.example.weathersphere.utils.NotificationManager.createNotificationChannel
import com.example.weathersphere.utils.checkNotificationPermission
import com.example.weathersphere.utils.requestNotificationPermission
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.firstOrNull

class WeatherWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = provideRepository()
            val weather = repository.getWeather().firstOrNull()
            if (weather != null) {
                repository.refreshWeather(LatLng(weather.lat, weather.lon))
                val updatedWeather = repository.getWeather().firstOrNull()
                updatedWeather?.let {
                    showNotification(updatedWeather)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun provideRepository(): WeatherRepository {
        val weatherApi = WeatherRemoteDataSource(RetrofitClient.apiService)
        val productDao = WeatherLocalDataSource(DatabaseProvider.getDatabase(context).weatherDao)
        return WeatherRepositoryImpl.getInstance(weatherApi, productDao)
    }

    private fun showNotification(weather: WeatherResponse) {
        createNotificationChannel()

        val notification = buildNotification(weather)

        if (checkNotificationPermission(context)) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(weather: WeatherResponse): Notification {
        val notificationContent = """"Good Morning
            |Temperature Today : ${weather.daily[0].temp}
            |Humidity  ${weather.current.humidity}
            |Alert: ${weather.alerts?.get(0)?.description}
        """.trimMargin()

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)

        return notificationBuilder.build()
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val CHANNEL_ID = "WeatherNotificationChannel"
        private const val CHANNEL_NAME = "Weather Notifications"
        private const val CHANNEL_DESCRIPTION = "Channel for weather notifications"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE = 0
    }
}