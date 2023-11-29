package algot.emil.api

import android.util.Log
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "WeatherApi"
private interface MeteoApi {
    @GET("v1/forecast")
    suspend fun getDailyWeatherForSevenDaysByCoordinates(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("daily") daily: String = "weather_code,temperature_2m_max"
    ): Response<WeatherData>


    @GET("v1/forecast")
    suspend fun getHourlyWeatherByDaysAndByCoordinates(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly") daily: String = "temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,wind_speed_10m,wind_direction_10m",
        @Query("forecast_days") days: Int = 2
    ): Response<HourlyWeatherData>

    @GET("v1/forecast")
    suspend fun getHourlyWeatherByDaysAndByCoordinatesAndStartAndEndDate(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly") daily: String = "temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,wind_speed_10m,wind_direction_10m",
        @Query("start_date") startDate: String, //format: 2023-11-30
        @Query("end_date") endDate: String //format: 2023-11-31
    ): Response<HourlyWeatherData>



}

private interface GeocodeApi {

    @GET("/search")
    suspend fun getCoordinatesAndDisplayName(
        @Query("q") place: String
    ): Response<List<PlaceData>>

}

object WeatherApi {

    private val geoInstance = Retrofit.geoCodeInstance.create(GeocodeApi::class.java)
    private val meteoInstance = Retrofit.meteoInstance.create(MeteoApi::class.java)

    suspend fun getDailyWeatherForSevenDays(
        latitude: Float, longitude: Float
    ): Response<WeatherData> {
        Log.d(TAG, "In API")
        return meteoInstance.getDailyWeatherForSevenDaysByCoordinates(latitude, longitude)
    }

    suspend fun getHourlyWeatherForTwoDays(
        latitude: Float, longitude: Float
    ): Response<HourlyWeatherData> {
        return meteoInstance.getHourlyWeatherByDaysAndByCoordinates(latitude, longitude)
    }

    /**
     * Time interval should be in format:  "2023-11-31"
     */
    suspend fun getHourlyWeatherWithTimeInterval(
        latitude: Float, longitude: Float, startDate:String, endDate: String
    ): Response<HourlyWeatherData> {
        return meteoInstance.getHourlyWeatherByDaysAndByCoordinatesAndStartAndEndDate(latitude, longitude, startDate=startDate,endDate=endDate)
    }

    suspend fun searchPlaces(query: String): Response<List<PlaceData>> {
        return geoInstance.getCoordinatesAndDisplayName(query)
    }

}
