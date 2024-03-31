package com.example.weathersphere.viewmodel

import app.cash.turbine.test
import com.example.fake_source.FakeWeatherRepository
import com.example.weathersphere.model.MainDispatcherRule
import com.example.weathersphere.model.data.Place
import com.example.weathersphere.model.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavouriteViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val place1: Place = Place(0, "al-obour city", 40.7128, -74.0060)
    private val place2: Place = Place(1, "Nasr city", 34.0522, -118.2437)
    private val place3: Place = Place(2, "Cairo city", 51.5074, -0.1278)

    private lateinit var repository: WeatherRepository
    private lateinit var favouriteViewModel: FavouriteViewModel

    @Before
    fun setUp() {
        repository = FakeWeatherRepository()
        favouriteViewModel = FavouriteViewModel(repository)
    }

    @Test
    fun insertPlaceToFavTest() = runTest {
        //when
        favouriteViewModel.insertToFavourite(place1)
        favouriteViewModel.getAllFavouritePlaces()
        var result: List<Place> = listOf()
        favouriteViewModel.favouritePlacesStateFlow.test {
            result = this.awaitItem()
        }

        //then
        Assert.assertTrue(result.contains(place1))
    }

    @Test
    fun deletePlaceFromFavTest() = runBlocking{
        //when
        favouriteViewModel.insertToFavourite(place1)
        favouriteViewModel.deleteFromFavourite(place1)
        favouriteViewModel.getAllFavouritePlaces()
        var result : List<Place> = listOf()
        favouriteViewModel.favouritePlacesStateFlow.test {
            result = this.awaitItem()
        }

        //then
        Assert.assertFalse(result.contains(place1))
    }

    @Test
    fun getAllPlacesTest()= runBlocking{
        //when
        favouriteViewModel.insertToFavourite(place1)
        favouriteViewModel.insertToFavourite(place2)
        favouriteViewModel.insertToFavourite(place3)
        favouriteViewModel.getAllFavouritePlaces()

        var result: List<Place> = listOf()
        favouriteViewModel.favouritePlacesStateFlow.test {
            result = this.awaitItem()
        }

        //then
        Assert.assertEquals(listOf(place1, place2, place3), result)
    }
}