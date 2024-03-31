package com.example.weathersphere.model.local


import com.example.fake_source.FakeWeatherDao
import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherLocalDataSourceImplTest {

    private val weatherResponse = WeatherResponse(
        0,
        Current(0, 0.0, 1, 0.0, 1, 1, 1, 1, 0.0, 0.0, 1, listOf(), 1, 0.0, 0.0),
        listOf(),
        listOf(),
        0.0,
        0.0,
        "",
        1,
        listOf()
    )

    private val place1 = Place(1, "Cairo city", 51.5074, -0.1278)

    private val alarm1 = WeatherAlarm(
        time = 1616995200000,
        kind = "Kind 1",
        latitude = 40.7128,
        longitude = -74.0060,
        zoneName = "Zone 1"
    )

    private lateinit var localDataSource: WeatherLocalDataSourceImpl

    @Before
    fun setUp() {
        localDataSource = WeatherLocalDataSourceImpl(FakeWeatherDao())
    }

    @Test
    fun `insertWeather should insert weather data`() = runTest {
        // Act
        localDataSource.insertWeather(weatherResponse)

        // Assert
        val weatherFlow: Flow<WeatherResponse?> = localDataSource.getWeather()
        val weatherList = weatherFlow.toList()
        assertEquals(1, weatherList.size)
        assertEquals(weatherResponse, weatherList[0])
    }

    @Test
    fun `insertPlaceToFavourite should insert a favourite place`() = runTest {
        // Act
        localDataSource.insertPlaceToFavourite(place1)

        // Assert
        val placesFlow: Flow<List<Place>> = localDataSource.getAllFavourite()
        val placesList = placesFlow.toList()
        assertEquals(1, placesList.size)
        assertEquals(listOf(place1), placesList[0])
    }

    @Test
    fun `insertAlarm should insert a weather alarm`() {
        // Act
        runTest {
            localDataSource.insertAlarm(alarm1)

            // Assert
            val alarmsFlow = localDataSource.getAllAlarms()
            val alarmsList = alarmsFlow.first()
            assertEquals(1, alarmsList.size)
            assertEquals(alarm1, alarmsList[0])
        }
    }

    @Test
    fun `deleteAlarm should delete a weather alarm`() {
        // Act
        runTest {
            localDataSource.insertAlarm(alarm1)
            localDataSource.deleteAlarm(alarm1)

            // Assert
            val alarmsFlow = localDataSource.getAllAlarms()
            val alarmsList = alarmsFlow.first()
            assertEquals(0, alarmsList.size)
        }
    }
}
