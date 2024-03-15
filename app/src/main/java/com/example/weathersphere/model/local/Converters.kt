package com.example.weathersphere.model.local

import androidx.room.TypeConverter
import com.example.weathersphere.model.data.City
import com.example.weathersphere.model.data.Clouds
import com.example.weathersphere.model.data.Coord
import com.example.weathersphere.model.data.ForecastItem
import com.example.weathersphere.model.data.Main
import com.example.weathersphere.model.data.Sys
import com.example.weathersphere.model.data.Weather
import com.example.weathersphere.model.data.Wind
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {

    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromCoord(coord: Coord): String {
        return gson.toJson(coord)
    }

    @TypeConverter
    @JvmStatic
    fun toCoord(json: String): Coord {
        val type = object : TypeToken<Coord>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromClouds(clouds: Clouds): String {
        return gson.toJson(clouds)
    }

    @TypeConverter
    @JvmStatic
    fun toClouds(json: String): Clouds {
        val type = object : TypeToken<Clouds>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromMain(main: Main): String {
        return gson.toJson(main)
    }

    @TypeConverter
    @JvmStatic
    fun toMain(json: String): Main {
        val type = object : TypeToken<Main>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromSys(sys: Sys): String {
        return gson.toJson(sys)
    }

    @TypeConverter
    @JvmStatic
    fun toSys(json: String): Sys {
        val type = object : TypeToken<Sys>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromWeatherList(weathers: List<Weather>): String {
        return gson.toJson(weathers)
    }

    @TypeConverter
    @JvmStatic
    fun toWeatherList(json: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromWind(wind: Wind): String {
        return gson.toJson(wind)
    }

    @TypeConverter
    @JvmStatic
    fun toWind(json: String): Wind {
        val type = object : TypeToken<Wind>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromCity(city: City): String {
        return gson.toJson(city)
    }

    @TypeConverter
    @JvmStatic
    fun toCity(cityString: String): City {
        return gson.fromJson(cityString, City::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<ForecastItem> {
        val listType = object : TypeToken<List<ForecastItem>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun toString(list: List<ForecastItem>): String {
        return gson.toJson(list)
    }
}