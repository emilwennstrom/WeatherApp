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


    @GET("v1/forecast")
    suspend fun getHourlyWeatherByDaysAndByCoordinates(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly") daily: String = "temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,wind_speed_10m,wind_direction_10m",
        @Query("forecast_days") days: Int=2
    ) : Response<HourlyWeatherData>




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

    suspend fun getHourlyWeatherForTwoDays(
        latitude: Float,
        longitude: Float
    ): Response<HourlyWeatherData> {

        val geoInstance = Retrofit.geoCodeInstance.create(IWeatherApi::class.java)


        val meteoInstance = Retrofit.meteoInstance.create(IWeatherApi::class.java)
        return meteoInstance.getHourlyWeatherByDaysAndByCoordinates(latitude, longitude)

    }

}
