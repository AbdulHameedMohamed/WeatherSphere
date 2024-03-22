package com.example.weathersphere.model.local

import androidx.room.*
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.Place
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceToFavourite(place: Place)

    @Delete
    suspend fun deletePlaceFromFavourite(place: Place)

    @Query("SELECT * FROM place")
    fun getAllFavouritePlaces(): Flow<List<Place>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(weatherAlarm: WeatherAlarm)

    @Delete
    suspend fun deleteAlarm(weatherAlarm: WeatherAlarm)

    @Query("SELECT * FROM alarm")
    fun getAllAlarms(): Flow<List<WeatherAlarm>>
}