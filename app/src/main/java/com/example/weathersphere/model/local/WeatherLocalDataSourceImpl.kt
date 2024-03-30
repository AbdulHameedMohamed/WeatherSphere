package com.example.weathersphere.model.local

import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(private val weatherDao: WeatherDao) : WeatherLocalDataSource {
    override fun getWeather(): Flow<WeatherResponse?> {
        return weatherDao.getWeather()
    }

    override fun insertWeather(weatherResponse: WeatherResponse) {
        weatherDao.insert(weatherResponse)
    }

    override suspend fun insertPlaceToFavourite(place: Place) {
        weatherDao.insertPlaceToFavourite(place)
    }
    override suspend fun deletePlaceFromFavourite(place: Place) {
        weatherDao.deletePlaceFromFavourite(place)
    }
    override fun getAllFavourite():Flow<List<Place>> {
        return weatherDao.getAllFavouritePlaces()
    }

    override suspend fun insertAlarm(weatherAlarm: WeatherAlarm) {
        weatherDao.insertAlarm(weatherAlarm)
    }

    override suspend fun deleteAlarm(weatherAlarm: WeatherAlarm) {
        weatherDao.deleteAlarm(weatherAlarm)
    }

    override fun getAllAlarms(): Flow<List<WeatherAlarm>> {
        return weatherDao.getAllAlarms()
    }
}