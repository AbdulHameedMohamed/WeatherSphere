package com.example.weathersphere.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarm")
data class WeatherAlarm(
    @PrimaryKey(autoGenerate = true)
    val time: Long,
    val kind: String,
    val latitude: Double,
    val longitude: Double,
    val zoneName: String = ""
): Serializable
