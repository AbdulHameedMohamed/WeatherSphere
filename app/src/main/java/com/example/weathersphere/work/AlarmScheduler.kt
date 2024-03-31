package com.example.weathersphere.work

import com.example.weathersphere.model.data.WeatherAlarm

interface AlarmScheduler {
    fun createAlarm(weatherAlarm: WeatherAlarm)
    fun cancelAlarm(weatherAlarm: WeatherAlarm)
}