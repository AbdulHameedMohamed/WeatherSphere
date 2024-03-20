package com.example.weathersphere.model

import android.util.Log
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.local.WeatherLocalDataSource
import com.example.weathersphere.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class WeatherRepository private constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) {
    private val _weatherFlow =
        MutableStateFlow<WeatherResult<WeatherResponse>>(WeatherResult.Loading)
    val weatherFlow: StateFlow<WeatherResult<WeatherResponse>> = _weatherFlow.asStateFlow()
    suspend fun getWeatherData() {
            localDataSource.getWeather().catch {
                Log.d(TAG, "getWeatherData: Fail" + it.message)

                _weatherFlow.value = WeatherResult.Error(it)
            }.collectLatest {
                _weatherFlow.value = WeatherResult.Success(it)
            }
    }

    suspend fun refreshWeather(lat: Double, lon: Double) {
        withContext(Dispatchers.IO) {
            runCatching {
                Log.d(TAG, "refreshWeather: ")
                val response = remoteDataSource.getWeather(lat = lat, lon = lon)
                Log.d(TAG, "refreshWeather: ${response.body()}")
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