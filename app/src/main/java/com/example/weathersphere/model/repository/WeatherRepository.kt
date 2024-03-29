package com.example.weathersphere.model.repository

import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRepository {
    fun getAllFavouritePlaces(): Flow<List<Place>>
    suspend fun addToFavourite(place: Place)
    suspend fun deleteFromFavourite(place: Place)
    fun getAllAlarms(): Flow<List<WeatherAlarm>>
    suspend fun insertAlarm(weatherAlarm: WeatherAlarm)
    suspend fun deleteAlarm(weatherAlarm: WeatherAlarm)
    suspend fun getWeather(): Flow<WeatherResponse>
    suspend fun refreshWeather(latLng: LatLng)
}