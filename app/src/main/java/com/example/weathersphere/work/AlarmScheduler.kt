package com.example.weathersphere.work

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.utils.Constants

class AlarmScheduler private constructor(private val application: Application) : AlarmSchedulerInterface {
    private val alarmManager: AlarmManager by lazy {
        application.getSystemService(AlarmManager::class.java)
    }

    companion object {
        private var instance: AlarmScheduler? = null

        fun getInstance(application: Application): AlarmScheduler =
            instance ?: synchronized(this) {
                instance ?: AlarmScheduler(application).also { instance = it }
            }
    }

    @SuppressLint("ScheduleExactAlarm")
    override fun createAlarm(weatherAlarm: WeatherAlarm) {
        val intent = Intent(application, AlarmReceiver::class.java)
            .putExtra(Constants.WEATHER_ALARM, weatherAlarm)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            weatherAlarm.time,
            PendingIntent.getBroadcast(
                application,
                weatherAlarm.time.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancelAlarm(weatherAlarm: WeatherAlarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                application,
                weatherAlarm.time.toInt(),
                Intent(application, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}