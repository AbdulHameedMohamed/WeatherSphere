package com.example.weathersphere.viewmodel

import com.example.fake_source.FakeWeatherRepository
import com.example.weathersphere.model.data.WeatherAlarm
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fake_source.FakeAlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After

@OptIn(ExperimentalCoroutinesApi::class)
class AlertViewModelTest {

    // Rule to run tasks synchronously
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Fake implementations
    private lateinit var fakeAlarmScheduler: FakeAlarmScheduler
    private lateinit var fakeRepository: FakeWeatherRepository

    // Subject under test
    private lateinit var viewModel: AlertViewModel


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Before
    fun setup() {
        setupFakeViewModel()

        Dispatchers.setMain(StandardTestDispatcher())
    }

    private fun setupFakeViewModel() {
        fakeAlarmScheduler = FakeAlarmScheduler()
        fakeRepository = FakeWeatherRepository()
        viewModel = AlertViewModel(fakeRepository, fakeAlarmScheduler)
    }

    @Test
    fun `test creating alarm`() = runTest {
        // Given
        val alarm = WeatherAlarm(0, "Kind", 36.45, 85.65)

        // When
        viewModel.createAlarm(alarm)

        // Then
        assertThat(fakeAlarmScheduler.scheduledAlarms.size, equalTo(1))
        assertThat(fakeAlarmScheduler.scheduledAlarms.first(), equalTo(alarm))
    }

    @Test
    fun `test deleting alarm`() = runTest {
        // Given
        val alarm = WeatherAlarm(0, "Kind", 36.45, 85.65)
        viewModel.createAlarm(alarm)

        // When
        viewModel.destroyAlarm(alarm)

        // Then
        assertThat(fakeAlarmScheduler.scheduledAlarms.size, equalTo(0))
    }

    @Test
    fun `test getting all alarms`() = runTest {
        // Given
        val alarm1 = WeatherAlarm(0, "Kind 1", 36.45, 85.65)
        val alarm2 = WeatherAlarm(1, "Kind 2", 85.63, 92.37)
        viewModel.createAlarm(alarm1)
        viewModel.createAlarm(alarm2)

        viewModel.getAllAlarms()

        // Wait for a short delay to allow getAllAlarms() to complete
        delay(100)

        // When
        val alarms = viewModel.alarmsStateFlow.first()

        // Then
        assertThat(alarms.size, equalTo(2))
        assertThat(alarms[0], equalTo(alarm1))
        assertThat(alarms[1], equalTo(alarm2))
    }



}