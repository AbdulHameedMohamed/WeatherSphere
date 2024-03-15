package com.example.weathersphere.model.remote

import com.example.weathersphere.model.data.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/data/2.5/forecast?appid=19ff4d50c5d14d2e6d195efec7a4192d")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") lang: String
    ): Response<ForecastResponse>
}