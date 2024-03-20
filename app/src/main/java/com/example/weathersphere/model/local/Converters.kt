package com.example.weathersphere.model.local

import androidx.room.TypeConverter
import com.example.weathersphere.model.data.Alert
import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.Daily
import com.example.weathersphere.model.data.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()
    @TypeConverter
    fun fromCurrentToString(current: Current): String {
        return gson.toJson(current)
    }

    @TypeConverter
    fun fromStringToCurrent(stringCurrent: String): Current {
        return gson.fromJson(stringCurrent, Current::class.java)
    }

    @TypeConverter
    fun fromDailyToString(daily: List<Daily>): String {
        return gson.toJson(daily)
    }

    @TypeConverter
    fun fromStringToDaily(stringDaily: String): List<Daily> {
        return gson.fromJson(stringDaily, object : TypeToken<List<Daily>>() {}.type)
    }


    @TypeConverter
    fun fromHourlyToString(hourly: List<Hourly>): String {
        return gson.toJson(hourly)
    }

    @TypeConverter
    fun fromStringToHourly(stringHourly: String): List<Hourly> {
        return Gson().fromJson(stringHourly, object : TypeToken<List<Hourly>>() {}.type)
    }


    @TypeConverter
    fun fromAlertToString(alert: List<Alert>?): String? {
        alert?.let {
            return gson.toJson(alert)
        }
        return null
    }

    @TypeConverter
    fun fromStringToAlert(stringAlert: String?): List<Alert>? {
        stringAlert?.let {
            return gson.fromJson(stringAlert, object : TypeToken<List<Alert>>() {}.type)
        }
        return null
    }

}