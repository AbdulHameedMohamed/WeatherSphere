package com.example.weathersphere.model.local

import androidx.room.*
import com.example.weathersphere.model.data.ForecastResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("select * from weather limit 1")
    fun getWeather(): Flow<ForecastResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weather: ForecastResponse)

    @Delete
    fun deleteThisWeather(weather: ForecastResponse)
}