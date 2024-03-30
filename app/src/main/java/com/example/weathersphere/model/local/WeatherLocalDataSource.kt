package com.example.weathersphere.model.local

import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getWeather(): Flow<WeatherResponse?>
    fun insertWeather(weatherResponse: WeatherResponse)

    suspend fun insertPlaceToFavourite(place: Place)

    suspend fun deletePlaceFromFavourite(place: Place)
    fun getAllFavourite(): Flow<List<Place>>

    suspend fun insertAlarm(weatherAlarm: WeatherAlarm)

    suspend fun deleteAlarm(weatherAlarm: WeatherAlarm)
    fun getAllAlarms(): Flow<List<WeatherAlarm>>
}