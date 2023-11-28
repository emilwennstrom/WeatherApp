package algot.emil.model

import algot.emil.PersistenceContext
import algot.emil.api.DailyUnits
import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.HourlyDataDisplay
import algot.emil.api.HourlyUnits
import algot.emil.api.WeatherApi
import algot.emil.api.WeatherConverter
import algot.emil.persistence.Weather
import android.util.Log
import kotlinx.coroutines.flow.Flow

class WeatherModel(persistenceContext: PersistenceContext) {
    private val weatherDao = persistenceContext.weatherDao
    val allWeather = weatherDao.getAll()


    var weatherDisplay: List<DailyWeatherDisplay> ?= null
    var displayUnit: DailyUnits?= null
    var temperatureUnit: String?="C"


    var weatherHourlyDisplay: List<HourlyDataDisplay> ?= null
    var hourlyUnits: HourlyUnits ?=null

    suspend fun fetchWeatherNextSevenDays(): Boolean {
        val city = "Stockholm"
        Log.d("GetWeatherResults: ", "starting API call")
        val result = WeatherApi.getDailyWeatherForSevenDays( 52.52F, 13.41F)
        // Checking the results
        Log.d("GetWeatherResults: ", result.body().toString())
        if (result.isSuccessful && result.body() != null) {
            val resultBody = result.body()!!  // Extract WeatherData from the response
            weatherDisplay = WeatherConverter().getDailyWeatherDisplay(resultBody)
            Log.d("GetWeatherResults:", "list of result converted: "+ weatherDisplay.toString())
            displayUnit = WeatherConverter().getDailyUnits(resultBody)
            Log.d("GetWeatherResults:", "daily units: "+ displayUnit.toString())
            temperatureUnit = displayUnit!!.temperature_2m_max

            replaceWeatherDataInDb()


            return true
        }
        Log.d("GetWeatherResults: ", result.body().toString())
        return false
    }

    suspend fun fetchWeatherNextHours(): Boolean {
        val city = "Stockholm"
        Log.d("GetWeatherResultsHourly: ", "starting API call")
        val result = WeatherApi.getHourlyWeatherForTwoDays( 52.52F, 13.41F)
        Log.d("GetWeatherResultsHourly: ", result.body().toString())  // Checking the results
        if (result.isSuccessful && result.body() != null) {
            val resultBody = result.body()!!  // Extract WeatherData from the response
            weatherHourlyDisplay = WeatherConverter().getHourlyWeatherDisplay(resultBody)
            Log.d("GetWeatherResultsHourly:", "list of result converted: "+ weatherHourlyDisplay.toString())
            hourlyUnits = WeatherConverter().getHourlyUnits(resultBody)
            Log.d("GetWeatherResultsHourly:", "daily units: "+ hourlyUnits.toString())
            //temperatureUnit = hourlyUnits!!.temperature_2m_max

            //replaceWeatherDataInDb()
            replaceHourlyWeatherDataInDb()
            return true
        }
        Log.d("GetWeatherResultsHourly: ", "error: " + result.body().toString())
        return false
    }


    private suspend fun replaceWeatherDataInDb() {
        weatherDao.deleteAll()
        var dayNumber = 1L;
        for (weather in weatherDisplay!!) {
            weatherDao.insert(weather = Weather(id = dayNumber++, time = weather.time, weatherState = weather.weather_State_code, temperature = weather.temperature_2m_max))
        }
    }

    private suspend fun replaceHourlyWeatherDataInDb() {
        //TODO implement:

        //weatherDao.deleteAll()
        //var dayNumber = 1L;
        //for (weather in weatherDisplay!!) {
        //    weatherDao.insert(weather = Weather(id = dayNumber++, time = weather.time, weatherState = weather.weather_State_code, temperature = weather.temperature_2m_max))
        //}
    }

    suspend fun insert(weather: Weather){
        Log.d("TAG", weather.time)
        weatherDao.insert(weather)
    }

    fun getWeather(id: Long): Flow<Weather> = weatherDao.get(id)


}