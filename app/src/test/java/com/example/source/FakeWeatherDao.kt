package com.example.source

import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.local.WeatherDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherDao : WeatherDao {

    private val weatherList = mutableListOf<WeatherResponse>()
    private val favouritePlacesList = mutableListOf<Place>()
    private val alarmList = mutableListOf<WeatherAlarm>()

    override fun getWeather(): Flow<WeatherResponse> = flow { emit(weatherList.first()) }

    override fun insert(weather: WeatherResponse) {
        weatherList.add(weather)
    }

    override fun deleteWeather(weather: WeatherResponse) {
        weatherList.remove(weather)
    }

    override suspend fun insertPlaceToFavourite(place: Place) {
        favouritePlacesList.add(place)
    }

    override suspend fun deletePlaceFromFavourite(place: Place) {
        favouritePlacesList.remove(place)
    }

    override fun getAllFavouritePlaces(): Flow<List<Place>> = flow { emit(favouritePlacesList) }

    override suspend fun insertAlarm(weatherAlarm: WeatherAlarm) {
        alarmList.add(weatherAlarm)
    }

    override suspend fun deleteAlarm(weatherAlarm: WeatherAlarm) {
        alarmList.remove(weatherAlarm)
    }

    override fun getAllAlarms(): Flow<List<WeatherAlarm>> = flow { emit(alarmList) }
}