package com.example.weathersphere.model.data

import com.google.gson.annotations.SerializedName

data class ForecastItem(
    val clouds: Clouds,
    val dt: Long,
    @SerializedName("dt_txt") val dateTimeText: String,
    val main: Main,
    val pop: Double,
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)