package com.example.weathersphere.model.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.filters.SmallTest
import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import com.example.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.hamcrest.Matchers.`is`
import org.junit.Assert
@RunWith(JUnit4::class)
@SmallTest
class WeatherDaoTest {

    private lateinit var weatherDao: WeatherDao
    private lateinit var database: WeatherDataBase

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()
        weatherDao = database.weatherDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert_Favourite_and_check_it`() = runTest {
        // Arrange
        val favourite = Place(0, cityName = "Cairo", latitude = 20.0, longitude = 30.0)

        // Act
        weatherDao.insertPlaceToFavourite(favourite)

        // Assert
        val result = weatherDao.getAllFavouritePlaces().first()
        assertThat(result.size, `is`(1))
    }

    @Test
    fun `getAllFavourite_insert_Favourite_and_check_the_size`() = runTest {
        // Arrange
        val favourite = Place(0, cityName = "Cairo", latitude = 20.0, longitude = 30.0)
        weatherDao.insertPlaceToFavourite(favourite)

        // Act
        val favourites = weatherDao.getAllFavouritePlaces()

        // Assert
        val result = favourites.first()
        assertThat(result.size, `is`(1))
    }

    @Test
    fun `testInsertAndGetWeather`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        val weatherResponse = WeatherResponse(
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

        // Act
        weatherDao.insert(weatherResponse)

        // Assert
        val retrievedWeather = weatherDao.getWeather().first()
        assertThat(weatherResponse, `is`(retrievedWeather))
    }

    @Test
    fun `testInsertAlarm_insertsAlarmIntoDatabase`() = runTest {
        // Arrange
        val weatherAlarm = WeatherAlarm(
            time = 123456789L,
            kind = "test",
            latitude = 0.0,
            longitude = 0.0,
            zoneName = "test zone"
        )

        // Act
        weatherDao.insertAlarm(weatherAlarm)

        // Assert
        val alarms = weatherDao.getAllAlarms().first()
        Assert.assertTrue(alarms.contains(weatherAlarm))
    }

    @Test
    fun `testDeleteAlarm_removesAlarmFromDatabase`() = runTest {
        // Arrange
        val weatherAlarm = WeatherAlarm(
            time = 123456789L,
            kind = "test",
            latitude = 0.0,
            longitude = 0.0,
            zoneName = "test zone"
        )
        weatherDao.insertAlarm(weatherAlarm)

        // Act
        weatherDao.deleteAlarm(weatherAlarm)

        // Assert
        val alarms = weatherDao.getAllAlarms().first()
        Assert.assertFalse(alarms.contains(weatherAlarm))
    }

    @Test
    fun `testGetAllAlarms_returnsAllAlarmsFromDatabase`() = runTest {
        // Arrange
        val alarm1 = WeatherAlarm(
            time = 123456789L,
            kind = "test",
            latitude = 0.0,
            longitude = 0.0,
            zoneName = "test zone"
        )
        val alarm2 = WeatherAlarm(
            time = 987654321L,
            kind = "test",
            latitude = 1.0,
            longitude = 1.0,
            zoneName = "test zone"
        )
        weatherDao.insertAlarm(alarm1)
        weatherDao.insertAlarm(alarm2)

        // Act
        val alarms = weatherDao.getAllAlarms().first()

        // Assert
        Assert.assertTrue(alarms.contains(alarm1))
        Assert.assertTrue(alarms.contains(alarm2))
    }
}