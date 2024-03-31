package com.example.fake_source

import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.local.WeatherLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherLocalDataSource : WeatherLocalDataSource {
    private val weatherData = MutableStateFlow<WeatherResponse?>(null)
    private val favouritePlaces = MutableStateFlow<List<Place>>(emptyList())
    private val alarms = MutableStateFlow<List<WeatherAlarm>>(emptyList())

    override fun getWeather(): Flow<WeatherResponse?> = weatherData

    override fun insertWeather(weatherResponse: WeatherResponse) {
        weatherData.value = weatherResponse
    }

    override suspend fun insertPlaceToFavourite(place: Place) {
        favouritePlaces.value = favouritePlaces.value + place
    }

    override suspend fun deletePlaceFromFavourite(place: Place) {
        favouritePlaces.value = favouritePlaces.value - place
    }

    override fun getAllFavourite(): Flow<List<Place>> = favouritePlaces

    override suspend fun insertAlarm(weatherAlarm: WeatherAlarm) {
        alarms.value = alarms.value + weatherAlarm
    }

    override suspend fun deleteAlarm(weatherAlarm: WeatherAlarm) {
        alarms.value = alarms.value - weatherAlarm
    }

    override fun getAllAlarms(): Flow<List<WeatherAlarm>> = alarms
}
