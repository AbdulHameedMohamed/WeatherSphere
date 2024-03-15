package com.example.weathersphere.model

sealed class WeatherResult<out T> {
    data class Success<out T>(val data: T) : WeatherResult<T>()
    data class Error(val exception: Throwable) : WeatherResult<Nothing>()
    data object Loading : WeatherResult<Nothing>()
}