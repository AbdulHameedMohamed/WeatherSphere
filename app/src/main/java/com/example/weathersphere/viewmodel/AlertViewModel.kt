package com.example.weathersphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weathersphere.model.WeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.work.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel(
    private val repository: WeatherRepository,
    private val alarmScheduler: AlarmScheduler
) :
    ViewModel() {

    private val _alarmsMutableStateFlow: MutableStateFlow<List<WeatherAlarm>> =
        MutableStateFlow(emptyList())
    val alarmsStateFlow: StateFlow<List<WeatherAlarm>> get() = _alarmsMutableStateFlow

    val weatherFlow: StateFlow<WeatherResult<WeatherResponse>>
        get() = repository.weatherFlow

    fun getWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeather()
        }
    }

    fun getAllAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms().collectLatest {
                _alarmsMutableStateFlow.value = it
            }
        }
    }

    fun createAlarm(weatherAlarm: WeatherAlarm) {
        insertAlarm(weatherAlarm)
        createAlarmScheduler(weatherAlarm)
    }

    fun insertAlarm(weatherAlarm: WeatherAlarm) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlarm(weatherAlarm)
        }

    fun createAlarmScheduler(weatherAlarm: WeatherAlarm) = alarmScheduler.createAlarm(weatherAlarm)

    fun destroyAlarm(weatherAlarm: WeatherAlarm) {
        deleteAlarm(weatherAlarm)
        cancelAlarmScheduler(weatherAlarm)
    }

    private fun deleteAlarm(weatherAlarm: WeatherAlarm) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlarm(weatherAlarm)
        }
    private fun cancelAlarmScheduler(weatherAlarm: WeatherAlarm) = alarmScheduler.cancelAlarm(weatherAlarm)

    class Factory(
        private val repository: WeatherRepository,
        private val alarmScheduler: AlarmScheduler
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertViewModel(repository, alarmScheduler) as T
            }
            return super.create(modelClass, extras)
        }
    }
}