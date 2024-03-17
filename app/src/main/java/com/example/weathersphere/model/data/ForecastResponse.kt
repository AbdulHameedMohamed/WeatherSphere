package com.example.weathersphere.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.weathersphere.model.local.Converters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather")
@TypeConverters(Converters::class)
data class ForecastResponse(
    @PrimaryKey
    val id: Int = 0,
    val city: City,
    @SerializedName("cnt")
    val count: Int = 0,
    val cod: String,
    @SerializedName("list")
    @ColumnInfo(name = "forecasts")
    val forecasts: List<ForecastItem>,
    val message: Double = 0.0
)