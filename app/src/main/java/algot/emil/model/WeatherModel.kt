package algot.emil.model

import algot.emil.PersistenceContext
import algot.emil.api.DailyUnits
import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.RetrofitHelper
import algot.emil.api.WeatherApi
import algot.emil.api.WeatherConverter
import algot.emil.persistence.Weather
import android.util.Log

class WeatherModel(persistenceContext: PersistenceContext) {
    private val weatherDao = persistenceContext.weatherDao
    val allWeather = weatherDao.getAll()
    var weatherDisplay: List<DailyWeatherDisplay> ?= null
        get() = field
    var displayUnit: DailyUnits ?= null
        get() = field

    var temperatureUnit: String?="C"

    suspend fun insert(weather: Weather){
        Log.d("TAG", weather.time)
        weatherDao.insert(weather)
    }

    suspend fun fetchWeatherNextSevenDays(): Boolean {
        val weatherApi = RetrofitHelper.getInstance().create(WeatherApi::class.java)
        Log.d("GetWeatherResults: ", "starting API call")
        val result = weatherApi.getDailyWeatherForSevenDays()
        if (result != null){
            // Checking the results
            Log.d("GetWeatherResults: ", result.body().toString())
            if (result.isSuccessful && result.body() != null) {
                val resultBody = result.body()!!  // Extract WeatherData from the response
                weatherDisplay = WeatherConverter().getDailyWeatherDisplay(resultBody)
                Log.d("GetWeatherResults:", "list of result converted: "+ weatherDisplay.toString())
                displayUnit = WeatherConverter().getDailyUnits(resultBody)
                Log.d("GetWeatherResults:", "daily units: "+ displayUnit.toString())
                temperatureUnit= displayUnit!!.temperature_2m_max
                return true
            } else {
                // Handle unsuccessful response or null body
                return false
            }
        }
        else{
            Log.d("GetWeatherResults:", result)
            return false
        }
        Log.d("GetWeatherResults: ", result.body().toString())
        return false
    }


}