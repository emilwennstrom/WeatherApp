package algot.emil.api
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


private interface IWeatherApi {
    @GET("v1/forecast")
    suspend fun getDailyWeatherForSevenDaysByCoordinates(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("daily") daily: String = "weather_code,temperature_2m_max"
    ) : Response<WeatherData>





}


object WeatherAPI {
    suspend fun getDailyWeatherForSevenDays(
        latitude: Float,
        longitude: Float
    ): Response<WeatherData> {

       val geoInstance = Retrofit.geoCodeInstance.create(IWeatherApi::class.java)



       val meteoInstance = Retrofit.meteoInstance.create(IWeatherApi::class.java)
       return meteoInstance.getDailyWeatherForSevenDaysByCoordinates(latitude, longitude)

    }

}
