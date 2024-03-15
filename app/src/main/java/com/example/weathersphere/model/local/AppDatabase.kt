package com.example.weathersphere.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weathersphere.model.data.ForecastResponse

@Database(entities = [ForecastResponse::class], version = 1, exportSchema = false)
abstract class WeatherDataBase : RoomDatabase() {
    abstract val weatherDao: WeatherDao
}
object DatabaseProvider {
    @Volatile
    private lateinit var INSTANCE: WeatherDataBase

    fun getDatabase(context: Context): WeatherDataBase {
        synchronized(WeatherDataBase::class.java) {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext, WeatherDataBase::class.java, "Weather"
                ).build()
            }
        }
        return INSTANCE
    }
}