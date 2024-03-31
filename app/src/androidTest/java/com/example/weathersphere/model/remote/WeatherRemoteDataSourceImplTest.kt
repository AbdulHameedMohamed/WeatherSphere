package com.example.weathersphere.model.remote
import com.example.weathersphere.model.data.WeatherResponse
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherRemoteDataSourceImplTest {

    private lateinit var remoteDataSource: WeatherRemoteDataSourceImpl
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        // Arrange
        apiService = object : ApiService {
            override suspend fun getWeatherResponse(latitude: Double, longitude: Double, language: String): Response<WeatherResponse> {
                // Mock a successful response with sample JSON
                val jsonResponse = """
                    {
                        "current": {
                            "temp": 25
                        },
                        "hourly": [],
                        "daily": [],
                        "lat": $latitude,
                        "lon": $longitude
                    }
                """.trimIndent()
                val weatherResponse = Gson().fromJson(jsonResponse, WeatherResponse::class.java)
                return Response.success(weatherResponse)
            }
        }
        remoteDataSource = WeatherRemoteDataSourceImpl(apiService)
    }

    @Test
    fun getWeather_shouldReturn_correctResponse() {
        // Arrange
        val latitude = 40.7128
        val longitude = -74.006
        val language = "en"

        // Act
        val response = runBlocking { remoteDataSource.getWeather(LatLng(latitude, longitude), language) }

        // Assert
        assertEquals(true, response.isSuccessful)
        val responseBody = response.body()
        assertNotNull(responseBody)
        assertEquals(latitude, responseBody?.lat)
        assertEquals(longitude, responseBody?.lon)
    }
}