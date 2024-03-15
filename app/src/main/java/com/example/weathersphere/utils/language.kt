package com.example.weathersphere.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

fun changeLanguageLocaleTo(lan: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lan)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

fun getLanguageLocale(): String {
    val defaultLocale = Locale.getDefault()

    return defaultLocale.language
}