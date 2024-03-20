package com.example.weathersphere.model.remote

import android.util.Log
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.utils.getLanguageLocale
import retrofit2.Response

class WeatherRemoteDataSource(private val apiService: ApiService) {
    suspend fun getWeather(lat: Double, lon: Double): Response<WeatherResponse> {
        Log.d("TAG", "getWeather: ${getLanguageLocale()}")
        return apiService.getWeatherResponse(latitude = lat, longitude = lon, getLanguageLocale())
    }
}