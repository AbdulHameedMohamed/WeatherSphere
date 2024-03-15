package com.example.weathersphere.model.remote

import android.util.Log
import com.example.weathersphere.model.data.ForecastResponse
import com.example.weathersphere.utils.getLanguageLocale
import retrofit2.Response

class WeatherRemoteDataSource(private val apiService: ApiService) {
    suspend fun getWeather(lat: String, lon: String): Response<ForecastResponse> {
        Log.d("TAG", "getWeather: ${getLanguageLocale()}")
        return apiService.getWeather(lat = lat, lon = lon, getLanguageLocale())
    }
}