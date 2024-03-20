package com.example.weathersphere.model.local

import com.example.weathersphere.model.data.WeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(private val weatherDao: WeatherDao) {
    fun getWeather(): Flow<WeatherResponse> {
        return weatherDao.getWeather()
    }

    fun insertWeather(weatherResponse: WeatherResponse) {
        weatherDao.insert(weatherResponse)
    }

    fun deleteWeather(weatherResponse: WeatherResponse) {
        weatherDao.insert(weatherResponse)
    }
}