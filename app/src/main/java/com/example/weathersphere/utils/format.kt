package com.example.weathersphere.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.weathersphere.R
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
fun formatLongToString(dateTimeInMillis: Long, pattern: String): String {
    val resultFormat = SimpleDateFormat(pattern, Locale.getDefault())
    val date = Date(dateTimeInMillis)
    return resultFormat.format(date)
}