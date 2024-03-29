package com.example.weathersphere.utils

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

fun changeLanguageLocaleTo(lan: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lan)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

fun getLanguageLocale(): String = Locale.getDefault().language