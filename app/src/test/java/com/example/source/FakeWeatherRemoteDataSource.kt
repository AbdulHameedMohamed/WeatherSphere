package com.example.source

import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

class FakeWeatherRemoteDataSource : WeatherRemoteDataSource {
    private val weatherResponse: WeatherResponse = WeatherResponse(
        0,
        Current(0, 0.0, 1, 0.0, 1, 1, 1, 1, 0.0, 0.0, 1, listOf(), 1, 0.0, 0.0),
        listOf(),
        listOf(),
        0.0,
        0.0,
        "",
        1,
        listOf()
    )
    override suspend fun getWeather(latLng: LatLng, lang: String): Response<WeatherResponse> {
        // Return a fake response
        return Response.success(weatherResponse)
    }
}