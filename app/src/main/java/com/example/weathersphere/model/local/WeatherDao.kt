package com.example.weathersphere.model.local

import androidx.room.*
import com.example.weathersphere.model.data.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("select * from weather")
    fun getWeather(): Flow<WeatherResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weather: WeatherResponse)

    @Delete
    fun deleteThisWeather(weather: WeatherResponse)
}