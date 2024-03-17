package com.example.weathersphere.model.data

import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("grnd_level") val groundLevel: Int,
    val humidity: Int,
    val pressure: Int,
    @SerializedName("sea_level") val seaLevel: Int,
    @SerializedName("temp") val temperature: Double,
    @SerializedName("temp_kf") val tempKf: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("temp_min") val tempMin: Double
)