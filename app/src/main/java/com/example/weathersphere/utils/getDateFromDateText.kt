package com.example.weathersphere.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun getDateFromDateText(dateTimeText: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateTime = LocalDateTime.parse(dateTimeText, formatter)
    return dateTime.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
}

fun getTimeFromDateText(dateTimeText: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateTime = LocalDateTime.parse(dateTimeText, formatter)
    return dateTime.format(DateTimeFormatter.ofPattern("H", Locale.getDefault()))
}

fun getDayNameFromDate(dateTimeText: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(dateTimeText, formatter)
    val dayOfWeek = dateTime.dayOfWeek
    return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
}