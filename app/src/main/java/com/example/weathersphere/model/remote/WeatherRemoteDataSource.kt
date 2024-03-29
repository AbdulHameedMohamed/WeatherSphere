package com.example.weathersphere.model.remote

import android.util.Log
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.utils.getLanguageLocale
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

class WeatherRemoteDataSource(private val apiService: ApiService) {
    suspend fun getWeather(latLng: LatLng, lang: String= getLanguageLocale()): Response<WeatherResponse> {
        return apiService.getWeatherResponse(latitude = latLng.latitude, longitude = latLng.longitude, lang)
    }
}