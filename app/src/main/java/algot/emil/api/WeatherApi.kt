package algot.emil.api
import retrofit2.Response
import retrofit2.http.GET


interface WeatherApi {
    @GET("v1/forecast?latitude=52.52&longitude=13.41&daily=weather_code,temperature_2m_max")
    suspend fun getQuotes() : Response<WeatherData>
}
