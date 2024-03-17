package com.example.weathersphere.model

import android.util.Log
import com.example.weathersphere.model.data.ForecastResponse
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class WeatherRepository private constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) {
    fun getWeatherData() = flow {
        localDataSource.getWeather().catch {
            Log.d(TAG, "getWeatherData: Fail" + it.message)

            emit(WeatherResult.Error(it))
        }.collectLatest {
            emit(WeatherResult.Success(it))
        }
    }

    suspend fun refreshWeather(lat: String, lon: String) {
        withContext(Dispatchers.IO) {
            runCatching {
                Log.d(TAG, "refreshWeather: ")
                val response = remoteDataSource.getWeather(lat = lat, lon = lon)
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        Log.d(TAG, "refreshWeather: $weatherResponse")
                        localDataSource.insertWeather(weatherResponse)
                    }
                }
            }.onFailure {
                Log.e(TAG, "refreshWeather: Error ${it.message}")
                it.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "WeatherRepository"
        private lateinit var repository: WeatherRepository
        fun getInstance(
            apiClient: WeatherRemoteDataSource,
            localDataSource: WeatherLocalDataSource
        ): WeatherRepository {
            if (!::repository.isInitialized)
                repository = WeatherRepository(apiClient, localDataSource)
            return repository
        }
    }
}