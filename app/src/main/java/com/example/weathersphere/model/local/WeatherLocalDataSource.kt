package com.example.weathersphere.model.local

import com.example.weathersphere.model.data.Place
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

    suspend fun insertPlaceToFavourite(place: Place) {
        weatherDao.insertPlaceToFavourite(place)
    }
    suspend fun deletePlaceFromFavourite(place: Place) {
        weatherDao.deletePlaceFromFavourite(place)
    }
    fun getAllFavourite():Flow<List<Place>> {
        return weatherDao.getAllFavouritePlaces()
    }
}