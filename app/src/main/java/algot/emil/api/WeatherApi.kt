package algot.emil.api
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getDailyWeatherForSevenDays(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("daily") daily: String = "weather_code,temperature_2m_max"
    ) : Response<WeatherData>


}
