package com.example.weathersphere.model.remote

import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.utils.getLanguageLocale
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getWeather(
        latLng: LatLng,
        lang: String = getLanguageLocale()
    ): Response<WeatherResponse>
}