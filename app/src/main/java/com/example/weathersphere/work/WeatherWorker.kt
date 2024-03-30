package com.example.weathersphere.work

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.local.DatabaseProvider
import com.example.weathersphere.model.local.WeatherLocalDataSourceImpl
import com.example.weathersphere.model.remote.RetrofitClient
import com.example.weathersphere.model.remote.WeatherRemoteDataSourceImpl
import com.example.weathersphere.model.repository.WeatherRepository
import com.example.weathersphere.model.repository.WeatherRepositoryImpl
import com.example.weathersphere.utils.Constants
import com.example.weathersphere.utils.NotificationManager.buildNotification
import com.example.weathersphere.utils.NotificationManager.createNotificationChannel
import com.example.weathersphere.utils.checkNotificationPermission
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.firstOrNull

class WeatherWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = provideRepository()
            val weather = repository.getWeather().firstOrNull()
            if (weather != null) {
                repository.refreshWeather(LatLng(weather.lat, weather.lon))
                val updatedWeather = repository.getWeather().firstOrNull()
                updatedWeather?.let {
                    showNotification(updatedWeather)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun provideRepository(): WeatherRepository {
        val weatherApi = WeatherRemoteDataSourceImpl(RetrofitClient.apiService)
        val productDao = WeatherLocalDataSourceImpl(DatabaseProvider.getDatabase(context).weatherDao)
        return WeatherRepositoryImpl.getInstance(weatherApi, productDao)
    }

    private fun showNotification(weather: WeatherResponse) {
        if (checkNotificationPermission(context)) {
            createNotificationChannel(context)
            val notification = buildNotification(context, weather)
            NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_ID, notification)
        }
    }
}