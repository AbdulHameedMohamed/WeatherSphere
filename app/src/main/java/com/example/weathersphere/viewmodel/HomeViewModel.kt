package com.example.weathersphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val repository: WeatherRepository
) : ViewModel() {
    val weatherFlow: StateFlow<WeatherResult<WeatherResponse>>
        get() = repository.weatherFlow

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    init {
        viewModelScope.launch {
            repository.getWeather()
        }
    }
    fun setSelectedLocation(location: LatLng) {
        _selectedLocation.value = location
    }

    fun getWeather(latLng: LatLng) = viewModelScope.launch {
        Log.d(TAG, "getWeather: ${latLng.latitude}")
        repository.refreshWeather(latLng)
    }

    fun addPlaceToFavourite(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPlaceToFavourite(place)
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