package com.example.weathersphere.model.data

import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lon")
    val longitude: Double
)