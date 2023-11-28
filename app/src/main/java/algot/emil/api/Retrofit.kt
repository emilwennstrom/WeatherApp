package algot.emil.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {



    //val baseUrl = "https://quotable.io/"

    private const val geoCodeURL =  "https://geocode.maps.co" //  /search?q={place}
    private const val openMeteoURL =  "https://api.open-meteo.com"

    val meteoInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(openMeteoURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val geoCodeInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(geoCodeURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }




}