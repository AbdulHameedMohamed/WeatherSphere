package com.example.weathersphere.view.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _favouritePlacesMutableStateFlow: MutableStateFlow<List<Place>> =
        MutableStateFlow(emptyList())
    val favouritePlacesStateFlow: StateFlow<List<Place>> get() = _favouritePlacesMutableStateFlow

    fun insertPlaceToFavourite(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPlaceToFavourite(place)
        }
    }

    fun deletePlaceFromFav(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlaceFromFavourite(place)
        }
    }

    fun getAllFavouritePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavouritePlaces().collectLatest {
                _favouritePlacesMutableStateFlow.value = it
            }
        }
    }

    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavouriteViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}