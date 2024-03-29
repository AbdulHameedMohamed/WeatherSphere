package com.example.weathersphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathersphere.model.repository.WeatherRepositoryImpl
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _favouritePlacesMutableStateFlow: MutableStateFlow<List<Place>> =
        MutableStateFlow(emptyList())
    val favouritePlacesStateFlow: StateFlow<List<Place>> get() = _favouritePlacesMutableStateFlow

    fun insertToFavourite(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavourite(place)
        }
    }

    fun deleteFromFavourite(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFromFavourite(place)
        }
    }

    fun getAllFavouritePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavouritePlaces().collectLatest {
                _favouritePlacesMutableStateFlow.value = it
            }
        }
    }

    class Factory(private val repository: WeatherRepositoryImpl) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavouriteViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}