package com.example.weathersphere.model.repository

import android.util.Log
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override suspend fun getWeather():Flow<WeatherResponse?> {
        return localDataSource.getWeather()
    }

    override suspend fun refreshWeather(latLng: LatLng, lang: String) {
        withContext(Dispatchers.IO) {
            runCatching {
                val response = remoteDataSource.getWeather(latLng, lang)
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        localDataSource.insertWeather(weatherResponse)
                    }
                }
            }.onFailure {
                Log.e(TAG, "refreshWeather: Error ${it.message}")
                it.printStackTrace()
            }
        }
    }

    override suspend fun addToFavourite(place: Place) {
        localDataSource.insertPlaceToFavourite(place)
    }

    override suspend fun deleteFromFavourite(place: Place) {
        localDataSource.deletePlaceFromFavourite(place)
    }

    override fun getAllFavouritePlaces(): Flow<List<Place>> {
        return localDataSource.getAllFavourite()
    }

    override suspend fun insertAlarm(weatherAlarm: WeatherAlarm) {
        localDataSource.insertAlarm(weatherAlarm)
    }

    override suspend fun deleteAlarm(weatherAlarm: WeatherAlarm) {
        localDataSource.deleteAlarm(weatherAlarm)
    }

    override fun getAllAlarms(): Flow<List<WeatherAlarm>> {
        return localDataSource.getAllAlarms()
    }

    companion object {
        private const val TAG = "WeatherRepository"
        private lateinit var repository: WeatherRepositoryImpl
        fun getInstance(
            apiClient: WeatherRemoteDataSource,
            localDataSource: WeatherLocalDataSource
        ): WeatherRepositoryImpl {
            if (!Companion::repository.isInitialized)
                repository = WeatherRepositoryImpl(apiClient, localDataSource)
            return repository
        }
    }
}