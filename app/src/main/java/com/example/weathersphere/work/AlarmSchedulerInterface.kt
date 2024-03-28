package com.example.weathersphere.work

import android.content.Context
import com.example.weathersphere.model.data.WeatherAlarm

interface AlarmSchedulerInterface {
    fun createAlarm(weatherAlarm: WeatherAlarm)
    fun cancelAlarm(weatherAlarm: WeatherAlarm)
}