package com.example.weathersphere.model.local


import com.example.source.FakeWeatherDao
import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

    private val place1= Place(1, "Cairo city", 51.5074, -0.1278)

    private val alarm1 = WeatherAlarm(
        time = 1616995200000,
        kind = "Kind 1",
        latitude = 40.7128,
        longitude = -74.0060,
        zoneName = "Zone 1"
    )

    @Test
    fun `test getWeather`() = runTest {
        val dataSource = WeatherLocalDataSourceImpl(FakeWeatherDao())

        dataSource.insertWeather(weatherResponse)

        // Fetch weather data and assert
        val weatherFlow: Flow<WeatherResponse?> = dataSource.getWeather()
        val weatherList = weatherFlow.toList()
        assertEquals(1, weatherList.size)
        assertEquals(weatherResponse, weatherList[0])
    }

    @Test
    fun `test getAllFavourite`() = runTest {
        val dataSource = WeatherLocalDataSourceImpl(FakeWeatherDao())

        // Assume we have a list of sample favourite places
        val samplePlaces = listOf(Place(2, "Cairo city", 51.5074, -0.1278))
        dataSource.insertPlaceToFavourite(samplePlaces[0])

        // Fetch favourite places and assert
        val placesFlow: Flow<List<Place>> = dataSource.getAllFavourite()
        val placesList = placesFlow.toList()
        assertEquals(1, placesList.size) // Assuming we inserted one sample place
        assertEquals(samplePlaces, placesList[0])
    }

    @Test
    fun `test insertAlarm`() {
        val dataSource = WeatherLocalDataSourceImpl(FakeWeatherDao())

        runTest {
            // Assume we have a sample weather alarm
            dataSource.insertAlarm(alarm1)

            // Fetch all alarms and ensure the sample alarm is inserted
            val alarmsFlow = dataSource.getAllAlarms()
            val alarmsList = alarmsFlow.first()
            assertEquals(1, alarmsList.size)
            assertEquals(alarm1, alarmsList[0])
        }
    }

    @Test
    fun `test insertPlaceToFavourite`() {
        val dataSource = WeatherLocalDataSourceImpl(FakeWeatherDao())

        runBlocking {

            // Insert the favourite place
            dataSource.insertPlaceToFavourite(place1)

            // Fetch all favourite places and ensure the sample place is inserted
            val placesFlow = dataSource.getAllFavourite()
            val placesList = placesFlow.first()
            assertEquals(1, placesList.size)
            assertEquals(place1, placesList[0])
        }
    }

    @Test
    fun `test deleteAlarm`() {
        val dataSource = WeatherLocalDataSourceImpl(FakeWeatherDao())

        runTest {
            // Assume we have a sample weather alarm
            dataSource.insertAlarm(alarm1)

            // Delete the alarm
            dataSource.deleteAlarm(alarm1)

            // Ensure the alarm is deleted
            val alarmsFlow = dataSource.getAllAlarms()
            val alarmsList = alarmsFlow.first()
            assertEquals(0, alarmsList.size)
        }
    }
}