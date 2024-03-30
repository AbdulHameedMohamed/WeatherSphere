package com.example.weathersphere.viewmodel

import kotlinx.coroutines.launch
import com.example.source.FakeWeatherRepository
import com.example.weathersphere.model.WeatherResult
import com.example.weathersphere.model.data.Current
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.data.WeatherResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var repository: FakeWeatherRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for testing
        repository = FakeWeatherRepository()
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher after testing
        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun `test getWeather`() = runTest {
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

        viewModel.getWeather(LatLng(0.0, 0.0), "en")
        delay(100) // Allow time for the coroutine to execute

        val weatherResult = viewModel.weatherFlow.value
        Assert.assertTrue(weatherResult is WeatherResult.Success)
        Assert.assertEquals(weatherResponse, (weatherResult as WeatherResult.Success).data)
    }

    @Test
    fun `test addPlaceToFavourite`() = runTest {
        val place = Place(0, "New York", 40.7128, -74.0060)

        viewModel.addPlaceToFavourite(place)
        delay(100) // Allow time for the coroutine to execute

        val favouritePlaces = repository.getAllFavouritePlaces().first()
        Assert.assertTrue(favouritePlaces.contains(place))
    }

    @Test
    fun `test setSelectedLocation`() = runTest {
        // Arrange

        // Assert initial state
        var initialLocation: LatLng? = null
        val initialJob = launch {
            viewModel.selectedLocation.collect { location ->
                initialLocation = location
            }
        }
        Assert.assertEquals(null, initialLocation)

        // Act: Set a new location
        val newLocation = LatLng(40.7128, -74.0060) // New York City coordinates
        viewModel.setSelectedLocation(newLocation)

        // Assert: Verify that the selectedLocation state flow changed
        val updatedLocation: LatLng? = viewModel.selectedLocation.first()
        Assert.assertEquals(newLocation, updatedLocation)

        // Cleanup
        initialJob.cancel()
    }
}