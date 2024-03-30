package com.example.weathersphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.repository.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _weatherFlow =
        MutableStateFlow<WeatherResult<WeatherResponse?>>(WeatherResult.Loading)
    val weatherFlow: StateFlow<WeatherResult<WeatherResponse?>> = _weatherFlow.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    init {
        viewModelScope.launch {
            repository.getWeather().catch {
                _weatherFlow.value = WeatherResult.Error(it)
            }.collectLatest { weather ->
                _weatherFlow.value = WeatherResult.Success(weather)
            }
        }
    }

    fun setSelectedLocation(location: LatLng) {
        _selectedLocation.value = location
    }

    fun getWeather(latLng: LatLng, lang: String) = viewModelScope.launch {
        repository.refreshWeather(latLng, lang)
    }

    fun addPlaceToFavourite(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavourite(place)
        }
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