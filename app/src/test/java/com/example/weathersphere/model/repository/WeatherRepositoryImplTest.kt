package com.example.weathersphere.model.repository

import com.example.source.FakeWeatherLocalDataSource
import com.example.source.FakeWeatherRemoteDataSource
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryImplTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var localDataSource: FakeWeatherLocalDataSource
    private lateinit var remoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        localDataSource = FakeWeatherLocalDataSource()
        remoteDataSource = FakeWeatherRemoteDataSource()
        repository = WeatherRepositoryImpl.getInstance(remoteDataSource, localDataSource)
    }

    @After
    fun tearDown() {
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun `test refreshWeather`() {
        runBlocking {
            val latLng = LatLng(30.36, 90.96)
            val lang = "en"
            repository.refreshWeather(latLng, lang)

            val weatherFlow = repository.getWeather()
            val weather = weatherFlow.first()
            // Check if weather is not null, indicating it was successfully refreshed
            assert(weather != null)
        }
    }

    @Test
    fun `test addToFavourite`() {
        runBlocking {
            val place = Place(0, "New York", 40.7128, -74.0060)
            repository.addToFavourite(place)

            val favouritePlacesFlow = repository.getAllFavouritePlaces()
            val favouritePlaces = favouritePlacesFlow.first()
            assertTrue(favouritePlaces.isNotEmpty())
            assertTrue(favouritePlaces.contains(place))
        }
    }

    @Test
    fun `test deleteFromFavourite`() {
        runBlocking {
            val place = Place(0, "Los Angeles", 34.0522, -118.2437)
            repository.addToFavourite(place)

            var favouritePlacesFlow = repository.getAllFavouritePlaces()
            var favouritePlaces = favouritePlacesFlow.first()
            assertTrue(favouritePlaces.contains(place))

            repository.deleteFromFavourite(place)

            favouritePlacesFlow = repository.getAllFavouritePlaces()
            favouritePlaces = favouritePlacesFlow.first()
            assertFalse(favouritePlaces.contains(place))
        }
    }

    @Test
    fun `test insertAndDeleteAlarm`() {
        runBlocking {
            val weatherAlarm = WeatherAlarm(0, "rain", 40.7128, -74.0060)
            repository.insertAlarm(weatherAlarm)

            val alarmsFlow = repository.getAllAlarms()
            val alarms = alarmsFlow.first()
            assertTrue(alarms.isNotEmpty())
            assertTrue(alarms.contains(weatherAlarm))

            repository.deleteAlarm(weatherAlarm)

            val updatedAlarmsFlow = repository.getAllAlarms()
            val updatedAlarms = updatedAlarmsFlow.first()
            assertFalse(updatedAlarms.contains(weatherAlarm))
        }
    }

    @Test
    fun `test getAllAlarms`() {
        runBlocking {
            val alarm1 = WeatherAlarm(0, "rain", 40.7128, -74.0060)
            val alarm2 = WeatherAlarm(1, "snow", 34.0522, -118.2437)
            val alarm3 = WeatherAlarm(2, "storm", 51.5074, -0.1278)

            repository.insertAlarm(alarm1)
            repository.insertAlarm(alarm2)
            repository.insertAlarm(alarm3)

            val alarmsFlow = repository.getAllAlarms()
            val alarms = alarmsFlow.first()
            assertTrue(alarms.contains(alarm1))
            assertTrue(alarms.contains(alarm2))
            assertTrue(alarms.contains(alarm3))
            assertEquals(3, alarms.size)
        }
    }

    @Test
    fun `test getAllFavouritePlaces`() {
        runBlocking {
            val place1 = Place(0, "New York", 40.7128, -74.0060)
            val place2 = Place(1, "Los Angeles", 34.0522, -118.2437)
            val place3 = Place(2, "London", 51.5074, -0.1278)

            repository.addToFavourite(place1)
            repository.addToFavourite(place2)
            repository.addToFavourite(place3)

            val favouritePlacesFlow = repository.getAllFavouritePlaces()
            val favouritePlaces = favouritePlacesFlow.first()

            assertTrue(favouritePlaces.contains(place1))
            assertTrue(favouritePlaces.contains(place2))
            assertTrue(favouritePlaces.contains(place3))
            assertEquals(3, favouritePlaces.size)
        }
    }
}