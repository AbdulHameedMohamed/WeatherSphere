package com.example.weathersphere.viewmodel

import app.cash.turbine.test
import com.example.source.FakeWeatherRepository
import com.example.weathersphere.model.MainDispatcherRule
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.repository.WeatherRepository
import com.example.weathersphere.work.AlarmScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.manipulation.Ordering.Context

class AlertViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dummyAlarm1 = WeatherAlarm(
        time = 1616995200000,
        kind = "Kind 1",
        latitude = 40.7128,
        longitude = -74.0060,
        zoneName = "Zone 1"
    )

    val dummyAlarm2 = WeatherAlarm(
        time = 1617078000000,
        kind = "Kind 2",
        latitude = 34.0522,
        longitude = -118.2437,
        zoneName = "Zone 2"
    )

    val dummyAlarm3 = WeatherAlarm(
        time = 1617160800000,
        kind = "Kind 3",
        latitude = 51.5074,
        longitude = -0.1278,
        zoneName = "Zone 3"
    )

    private lateinit var repository: WeatherRepository
    private lateinit var alertViewModel: AlertViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = FakeWeatherRepository()
        //TODO InitViewModel
    }

    @Test
    fun addAlarm() = runBlocking{
        //when
        alertViewModel.insertAlarm(dummyAlarm1)
        alertViewModel.getAllAlarms()
        var result: List<WeatherAlarm> = listOf()
        alertViewModel.alarmsStateFlow.test {
            result = this.awaitItem()
        }

        //then
        Assert.assertTrue(result.contains(dummyAlarm1))
    }



    @Test
    fun deletePlaceFromFavTest() = runBlocking{
        //when
        alertViewModel.insertAlarm(dummyAlarm1)
        alertViewModel.deleteAlarm(dummyAlarm1)
        alertViewModel.getAllAlarms()
        var result : List<WeatherAlarm> = listOf()
        alertViewModel.alarmsStateFlow.test {
            result = this.awaitItem()
        }

        //then
        Assert.assertFalse(result.contains(dummyAlarm1))
    }

    @Test
    fun getAllPlacesTest()= runBlocking{
        //when
        alertViewModel.insertAlarm(dummyAlarm1)
        alertViewModel.insertAlarm(dummyAlarm2)
        alertViewModel.insertAlarm(dummyAlarm3)
        alertViewModel.getAllAlarms()

        var result: List<WeatherAlarm> = listOf()
        alertViewModel.alarmsStateFlow.test {
            result = this.awaitItem()
        }

        //then
        Assert.assertEquals(listOf(dummyAlarm1, dummyAlarm2, dummyAlarm3), result)
    }
}