package com.example.fake_source

import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.work.AlarmScheduler

class FakeAlarmScheduler : AlarmScheduler {
    val scheduledAlarms = mutableListOf<WeatherAlarm>()

    override fun createAlarm(weatherAlarm: WeatherAlarm) {
        scheduledAlarms.add(weatherAlarm)
    }

    override fun cancelAlarm(weatherAlarm: WeatherAlarm) {
        scheduledAlarms.remove(weatherAlarm)
    }
}