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
    val sdf = SimpleDateFormat("EEEE", Locale(lang))
    val calendar: Calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.timeInMillis = timestamp.toLong() * 1000
    return when (calendar.get(Calendar.DAY_OF_YEAR)) {
        currentDay -> context.getString(R.string.today)
        currentDay + 1 -> context.getString(R.string.tomorrow)
        else -> {
            val dayOfWeek = sdf.format(calendar.time)
            dayOfWeek.substring(0, 1).uppercase(Locale.getDefault()) + dayOfWeek.substring(1)
        }
    }
}

fun ImageView.setIconFromApi(iconId: String) {
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


fun setIcon(id: String, iv: ImageView) {
    when (id) {
        "01d" -> iv.setImageResource(R.drawable.sun)
        "02d" -> iv.setImageResource(R.drawable._02d)
        "03d" -> iv.setImageResource(R.drawable._03d)
        "04d" -> iv.setImageResource(R.drawable._04n)
        "09d" -> iv.setImageResource(R.drawable._09n)
        "10d" -> iv.setImageResource(R.drawable._10d)
        "11d" -> iv.setImageResource(R.drawable._11d)
        "13d" -> iv.setImageResource(R.drawable._13d)
        "50d" -> iv.setImageResource(R.drawable._50d)
        "01n" -> iv.setImageResource(R.drawable._01n)
        "02n" -> iv.setImageResource(R.drawable._02n)
        "03n" -> iv.setImageResource(R.drawable._03d)
        "04n" -> iv.setImageResource(R.drawable._04n)
        "09n" -> iv.setImageResource(R.drawable._09n)
        "10n" -> iv.setImageResource(R.drawable._10n)
        "11n" -> iv.setImageResource(R.drawable._11d)
        "13n" -> iv.setImageResource(R.drawable._13d)
        "50n" -> iv.setImageResource(R.drawable._50d)
        else -> iv.setImageResource(R.drawable._load)
    }
}

fun fromUnixToString(time: Int, lang: String): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale(lang))
    val date = Date(time * 1000L)
    return sdf.format(date).uppercase(Locale.ROOT)
}

fun fromUnixToStringTime(time: Int, lang: String): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale(lang))
    val date = Date(time * 1000L)
    return sdf.format(date).uppercase(Locale.ROOT)
}