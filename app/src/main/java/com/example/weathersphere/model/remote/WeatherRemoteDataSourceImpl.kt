package com.example.weathersphere.model.remote

import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.utils.getLanguageLocale
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response


class WeatherRemoteDataSourceImpl(private val apiService: ApiService) : WeatherRemoteDataSource {
    override suspend fun getWeather(latLng: LatLng, lang: String): Response<WeatherResponse> {
        return apiService.getWeatherResponse(latitude = latLng.latitude, longitude = latLng.longitude, lang)
    }
}