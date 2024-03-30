package com.example.weathersphere.model.remote
import com.example.weathersphere.model.data.WeatherResponse
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherRemoteDataSourceImplTest {

    private lateinit var remoteDataSource: WeatherRemoteDataSourceImpl
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
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
    fun test_getWeather() {
        runBlocking {
            // Define latitude, longitude, and language for testing
            val latitude = 40.7128
            val longitude = -74.006
            val language = "en"

            // Call the method being tested
            val response = remoteDataSource.getWeather(LatLng(latitude, longitude), language)

            // Check if the response is successful
            assertEquals(true, response.isSuccessful)

            // Check if the response body is not null
            val responseBody = response.body()
            assertEquals(false, responseBody == null)

            // Assert other properties of the response body if needed
            assertEquals(latitude, responseBody?.lat)
            assertEquals(longitude, responseBody?.lon)
        }
    }
}