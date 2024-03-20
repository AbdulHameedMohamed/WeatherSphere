package com.example.weathersphere.model.remote

import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall?units=metric&appid=${Constants.API_KEY}")
    suspend fun getWeatherResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String = "en"
    ): Response<WeatherResponse>
}