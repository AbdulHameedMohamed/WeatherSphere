package com.example.weathersphere.model.local

import com.example.weathersphere.model.data.ForecastResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(private val weatherDao: WeatherDao) {
    fun getWeather(): Flow<ForecastResponse> {
        return weatherDao.getWeather()
    }

    fun insertWeather(weatherResponse: ForecastResponse) {
        weatherDao.insert(weatherResponse)
    }

    fun deleteWeather(weatherResponse: ForecastResponse) {
        weatherDao.insert(weatherResponse)
    }
}