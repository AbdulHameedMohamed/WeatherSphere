package com.example.weathersphere.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.weathersphere.R
import com.example.weathersphere.model.data.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatDayOfWeek(timestamp: Int, context: Context, lang: String): String {
    val sdf = SimpleDateFormat("EEE", Locale(lang))
    val calendar: Calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.timeInMillis = timestamp.toLong() * 1000
    return when (calendar.get(Calendar.DAY_OF_YEAR)) {
        currentDay -> context.getString(R.string.today)
        currentDay + 1 -> context.getString(R.string.tomorrow)
        else -> sdf.format(calendar.time).uppercase(Locale.ROOT)
    }
}

fun ImageView.setIconFromApi(iconId: String){

    val urlString = "https://openweathermap.org/img/wn/$iconId@2x.png"
    Glide.with(this)
        .load(urlString)
        .error(R.drawable._03d)
        .into(this)
}

fun formatDateStamp(timestamp: Long, context: Context, lang: String): String {
    val sdf = SimpleDateFormat("d MMM", Locale(lang))
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.timeInMillis = timestamp * 1000
    return when (calendar.get(Calendar.DAY_OF_YEAR)) {
        currentDay -> context.getString(R.string.today)
        currentDay + 1 -> context.getString(R.string.tomorrow)
        else -> sdf.format(calendar.time)
    }
}

fun formatTimestamp(timestamp: Long, lang: String): String {
    val sdf = SimpleDateFormat("h a", Locale(lang))
    val date = Date(timestamp * 1000)
    return sdf.format(date)
}
fun formatMillisToDateTimeString(dateTimeInMillis: Long, pattern: String): String {
    val resultFormat = SimpleDateFormat(pattern, Locale.getDefault())
    val date = Date(dateTimeInMillis)
    return resultFormat.format(date)
}

fun dateTimeStringToMillis(date: String, time: String): Long {
    val dateTimeFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
    val dateTimeString = "$date $time"
    val dateTime = dateTimeFormat.parse(dateTimeString)
    return dateTime?.time ?: -1
}

@SuppressLint("SetTextI18n")
fun setLocationNameByGeoCoder(weatherResponse: WeatherResponse, context: Context): String {
    try {
        val x =
            Geocoder(context).getFromLocation(
                weatherResponse.lat,
                weatherResponse.lon,
                5
            )

        return if (x != null && x[0].locality != null) {
            x[0].locality
        } else {
            weatherResponse.timezone
        }
    } catch (e: Exception) {
        return weatherResponse.timezone
    }
}

fun formatHourMinuteToString(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(calendar.time)
}