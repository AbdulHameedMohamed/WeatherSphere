package com.example.weathersphere.model.repository

import com.example.fake_source.FakeWeatherLocalDataSource
import com.example.fake_source.FakeWeatherRemoteDataSource
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
import org.junit.Assert.assertNotNull
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
    fun `refreshWeather should update weather data`() {
        runBlocking {
            // Arrange
            val latLng = LatLng(30.36, 90.96)
            val lang = "en"

            // Act
            repository.refreshWeather(latLng, lang)

            // Assert
            val weatherFlow = repository.getWeather()
            val weather = weatherFlow.first()
            assertNotNull(weather)
        }
    }

    @Test
    fun `addToFavourite should add a place to favourites`() {
        runBlocking {
            // Arrange
            val place = Place(0, "New York", 40.7128, -74.0060)

            // Act
            repository.addToFavourite(place)

            // Assert
            val favouritePlacesFlow = repository.getAllFavouritePlaces()
            val favouritePlaces = favouritePlacesFlow.first()
            assertTrue(favouritePlaces.isNotEmpty())
            assertTrue(favouritePlaces.contains(place))
        }
    }

    @Test
    fun `deleteFromFavourite should remove a place from favourites`() {
        runBlocking {
            // Arrange
            val place = Place(0, "Los Angeles", 34.0522, -118.2437)
            repository.addToFavourite(place)

            // Act
            repository.deleteFromFavourite(place)

            // Assert
            val favouritePlacesFlow = repository.getAllFavouritePlaces()
            val favouritePlaces = favouritePlacesFlow.first()
            assertFalse(favouritePlaces.contains(place))
        }
    }

    @Test
    fun `insertAndDeleteAlarm should insert and delete a weather alarm`() {
        runBlocking {
            // Arrange
            val weatherAlarm = WeatherAlarm(0, "rain", 40.7128, -74.0060)

            // Act
            repository.insertAlarm(weatherAlarm)

            // Assert
            val alarmsFlow = repository.getAllAlarms()
            val alarms = alarmsFlow.first()
            assertTrue(alarms.isNotEmpty())
            assertTrue(alarms.contains(weatherAlarm))

            // Act
            repository.deleteAlarm(weatherAlarm)

            // Assert
            val updatedAlarmsFlow = repository.getAllAlarms()
            val updatedAlarms = updatedAlarmsFlow.first()
            assertFalse(updatedAlarms.contains(weatherAlarm))
        }
    }

    @Test
    fun `getAllAlarms should return all weather alarms`() {
        runBlocking {
            // Arrange
            val alarm1 = WeatherAlarm(0, "rain", 40.7128, -74.0060)
            val alarm2 = WeatherAlarm(1, "snow", 34.0522, -118.2437)
            val alarm3 = WeatherAlarm(2, "storm", 51.5074, -0.1278)
            repository.insertAlarm(alarm1)
            repository.insertAlarm(alarm2)
            repository.insertAlarm(alarm3)

            // Act
            val alarmsFlow = repository.getAllAlarms()
            val alarms = alarmsFlow.first()

            // Assert
            assertTrue(alarms.contains(alarm1))
            assertTrue(alarms.contains(alarm2))
            assertTrue(alarms.contains(alarm3))
            assertEquals(3, alarms.size)
        }
    }

    @Test
    fun `getAllFavouritePlaces should return all favourite places`() {
        runBlocking {
            // Arrange
            val place1 = Place(0, "New York", 40.7128, -74.0060)
            val place2 = Place(1, "Los Angeles", 34.0522, -118.2437)
            val place3 = Place(2, "London", 51.5074, -0.1278)
            repository.addToFavourite(place1)
            repository.addToFavourite(place2)
            repository.addToFavourite(place3)

            // Act
            val favouritePlacesFlow = repository.getAllFavouritePlaces()
            val favouritePlaces = favouritePlacesFlow.first()

            // Assert
            assertTrue(favouritePlaces.contains(place1))
            assertTrue(favouritePlaces.contains(place2))
            assertTrue(favouritePlaces.contains(place3))
            assertEquals(3, favouritePlaces.size)
        }
    }
}