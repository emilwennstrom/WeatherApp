package algot.emil.api

import android.util.Log
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


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


}

private interface GeocodeApi {

    @GET("/search")
    suspend fun getCoordinatesAndDisplayName(
        @Query("q") place: String
    ): Response<List<PlaceData>>

}

object WeatherApi {
    suspend fun getDailyWeatherForSevenDays(
        latitude: Float, longitude: Float
    ): Response<WeatherData> {

        val geoInstance = Retrofit.geoCodeInstance.create(GeocodeApi::class.java)

        val data = geoInstance.getCoordinatesAndDisplayName("Repslagaregatan 5b Nyköping")

        Log.d("TAG", data.body().toString())

        val firstData = data.body()?.get(0)

        val lat = firstData?.lat?.toFloat()
        val long = firstData?.lon?.toFloat()


        val meteoInstance = Retrofit.meteoInstance.create(MeteoApi::class.java)
        if (lat != null && long != null) return meteoInstance.getDailyWeatherForSevenDaysByCoordinates(
            lat,
            long
        )
        else throw Exception()

    }

    suspend fun getHourlyWeatherForTwoDays(
        latitude: Float, longitude: Float
    ): Response<HourlyWeatherData> {

        val geoInstance = Retrofit.geoCodeInstance.create(GeocodeApi::class.java)


        val meteoInstance = Retrofit.meteoInstance.create(MeteoApi::class.java)
        return meteoInstance.getHourlyWeatherByDaysAndByCoordinates(latitude, longitude)

    }



}
