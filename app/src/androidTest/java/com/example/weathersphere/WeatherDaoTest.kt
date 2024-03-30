package com.example.weathersphere

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.filters.SmallTest
import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherAlarm
import com.example.weathersphere.model.data.WeatherResponse
import com.example.weathersphere.model.local.WeatherDao
import com.example.weathersphere.model.local.WeatherDataBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    fun insert_Favourite_and_check_it() = runBlocking {
        //arrange
        val favourate = Place(0, cityName = "cairo", latitude = 20.0, longitude = 30.0)
        //act
        weatherDao.insertPlaceToFavourite(favourate)
        //assert
        val result = weatherDao.getAllFavouritePlaces().first()
        assertThat(result.size, `is`(1))
    }

    @Test
    fun getAllFavourite_insert_Favourite_and_check_the_size() = runBlocking {
        //arrange
        val favourite = Place(0, cityName = "cairo", latitude = 20.0, longitude = 30.0)
        weatherDao.insertPlaceToFavourite(favourite)
        //act
        val favourites= weatherDao.getAllFavouritePlaces()
        //assert
        val result = favourites.first()
        assertThat(result.size, `is`(1))
    }

    @Test
    fun testInsertAndGetWeather() = runBlocking {
        // Create a dummy weather response
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

        // Insert the weather response into the database
        weatherDao.insert(weatherResponse)

        // Get the weather response from the database
        val retrievedWeather = weatherDao.getWeather().first()

        // Assert that the retrieved weather is the same as the inserted one
        assertThat(weatherResponse, `is`(retrievedWeather))
    }

    @Test
    fun testInsertAlarm_insertsAlarmIntoDatabase() = runBlocking {
        val weatherAlarm = WeatherAlarm(
            time = 123456789L,
            kind = "test",
            latitude = 0.0,
            longitude = 0.0,
            zoneName = "test zone"
        )

        weatherDao.insertAlarm(weatherAlarm)

        val alarms = weatherDao.getAllAlarms().first()

        Assert.assertTrue(alarms.contains(weatherAlarm))
    }

    @Test
    fun testDeleteAlarm_removesAlarmFromDatabase() = runBlocking {
        val weatherAlarm = WeatherAlarm(
            time = 123456789L,
            kind = "test",
            latitude = 0.0,
            longitude = 0.0,
            zoneName = "test zone"
        )

        weatherDao.insertAlarm(weatherAlarm)
        weatherDao.deleteAlarm(weatherAlarm)

        val alarms = weatherDao.getAllAlarms().first()

        Assert.assertFalse(alarms.contains(weatherAlarm))
    }

    @Test
    fun testGetAllAlarms_returnsAllAlarmsFromDatabase() = runBlocking {
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

        val alarms = weatherDao.getAllAlarms().first()

        Assert.assertTrue(alarms.contains(alarm1))
        Assert.assertTrue(alarms.contains(alarm2))
    }

}