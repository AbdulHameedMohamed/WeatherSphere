package com.example.weathersphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.data.ForecastResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val repository: WeatherRepository
) : ViewModel() {
    val weatherFlow: StateFlow<WeatherResult<ForecastResponse>>
        get() = repository.weatherFlow
    init {
        viewModelScope.launch {
            repository.getWeatherData()
        }
    }

    fun getWeather(lat: Double, long: Double) = viewModelScope.launch {
        Log.d(TAG, "getWeather: $lat")
        repository.refreshWeather("$lat", "$long")
    }

    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}