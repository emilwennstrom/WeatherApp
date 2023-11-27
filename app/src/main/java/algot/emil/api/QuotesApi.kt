package algot.emil.api
import retrofit2.Response
import retrofit2.http.GET


interface QuotesApi {
    //@GET("/quotes")

    @GET("/v1/forecast?latitude=52.52&longitude=13.41&daily=temperature_2m_max,rain_sum,showers_sum,snowfall_sum")
    suspend fun getQuotes() : Response<DailyUnits>
}
