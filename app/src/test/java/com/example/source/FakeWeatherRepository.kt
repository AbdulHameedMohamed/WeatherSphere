package com.example.source

import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.repository.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherRepository(private val places: MutableList<Place> = mutableListOf(),
    private val alarms: MutableList<WeatherAlarm> = mutableListOf()): WeatherRepository {

        private val weatherResponse: WeatherResponse = WeatherResponse(
            0,
            Current(0, 0.0, 1, 0.0, 1, 1, 1, 1, 0.0, 0.0, 1, listOf(), 1, 0.0, 0.0),
            listOf(),
            listOf(),
            0.0,
            0.0,
            "",
            1,
            listOf()
        )

        override fun getAllFavouritePlaces(): Flow<List<Place>> {
            return flowOf(places)
        }

        override suspend fun addToFavourite(place: Place) {
        places.add(place)
    }

    override suspend fun deleteFromFavourite(place: Place) {
        places.remove(place)
    }

    override fun getAllAlarms(): Flow<List<WeatherAlarm>> {
        return flowOf(alarms)
    }

    override suspend fun insertAlarm(weatherAlarm: WeatherAlarm) {
        alarms.add(weatherAlarm)
    }

    override suspend fun deleteAlarm(weatherAlarm: WeatherAlarm) {
        alarms.remove(weatherAlarm)
    }

    override suspend fun getWeather(): Flow<WeatherResponse> {
        return flow {
            emit(weatherResponse) // Emit the fake weather response
        }
    }

    override suspend fun refreshWeather(latLng: LatLng, lang: String) {
        // Do nothing for now in the fake implementation
    }
}