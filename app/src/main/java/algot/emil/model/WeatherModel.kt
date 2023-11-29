package algot.emil.model

import algot.emil.PersistenceContext
import algot.emil.api.DailyUnits
import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.HourlyDataDisplay
import algot.emil.api.HourlyUnits
import algot.emil.api.PlaceData
import algot.emil.api.WeatherApi
import algot.emil.api.WeatherConverter
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherHourly
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

class WeatherModel(persistenceContext: PersistenceContext, connectivity: ConnectivityManager) {
    private val weatherDao = persistenceContext.weatherDao
    val allWeather = weatherDao.getAll()
    private val weatherHourlyDao = persistenceContext.weatherHourlyDao
    val allWeatherHourly = weatherHourlyDao.getAll()
    private val connectivityManager = connectivity

    var weatherDisplay: List<DailyWeatherDisplay>? = null
    var displayUnit: DailyUnits? = null
    var temperatureUnit: String? = "C"


    var weatherHourlyDisplay: List<HourlyDataDisplay>? = null
    var hourlyUnits: HourlyUnits? = null

    private suspend fun fetchWeatherNextSevenDays(lat: Float, lon: Float): Boolean {
        if(isNetworkAvailable()){
            Log.d("GetWeatherResults: ", "starting API call")
            val result = WeatherApi.getDailyWeatherForSevenDays(lat, lon)
            // Checking the results
            Log.d("GetWeatherResults: ", result.body().toString())
            if (result.isSuccessful && result.body() != null) {
                val resultBody = result.body()!!  // Extract WeatherData from the response
                weatherDisplay = WeatherConverter().getDailyWeatherDisplay(resultBody)
                Log.d("GetWeatherResults:", "list of result converted: " + weatherDisplay.toString())
                displayUnit = WeatherConverter().getDailyUnits(resultBody)
                Log.d("GetWeatherResults:", "daily units: " + displayUnit.toString())
                temperatureUnit = displayUnit!!.temperature_2m_max

                replaceWeatherDataInDb()


                return true
            }
            return false
        }else{
            weatherDao.getAll()
            return true
        }


    }


    private suspend fun fetchWeatherNextHours(lat: Float, lon: Float): Boolean {
        if(isNetworkAvailable()){
            Log.d("GetWeatherResultsHourly: ", "starting API call")
            val result = WeatherApi.getHourlyWeatherForTwoDays(lat, lon)
            Log.d("GetWeatherResultsHourly: ", result.body().toString())  // Checking the results
            if (result.isSuccessful && result.body() != null) {
                val resultBody = result.body()!!  // Extract WeatherData from the response
                weatherHourlyDisplay = WeatherConverter().getHourlyWeatherDisplay(resultBody)
                Log.d(
                    "GetWeatherResultsHourly:",
                    "list of result converted: " + weatherHourlyDisplay.toString()
                )
                hourlyUnits = WeatherConverter().getHourlyUnits(resultBody)
                Log.d("GetWeatherResultsHourly:", "daily units: " + hourlyUnits.toString())
                //temperatureUnit = hourlyUnits!!.temperature_2m_max
                replaceHourlyWeatherDataInDb()
                return true
            }
            return false
        }else{
            weatherHourlyDao.getAll()
            return true
        }

    }

    suspend fun fetchWeatherData(lat: Float, lon: Float): Pair<Boolean, Boolean> {
        Log.d("MODEL", "FETCHING")
        val isSevenDayFetched = fetchWeatherNextSevenDays(lat, lon)
        val isNextHoursFetched = fetchWeatherNextHours(lat, lon)
        return Pair(isSevenDayFetched, isNextHoursFetched)
    }


    private suspend fun replaceWeatherDataInDb() {
        Log.d("Model", "Replacing data")
        weatherDao.deleteAll()
        var dayNumber = 1L
        for (weather in weatherDisplay!!) {
            weatherDao.insert(
                weather = Weather(
                    id = dayNumber++,
                    time = weather.time,
                    weatherState = weather.weather_State_code,
                    temperature = weather.temperature_2m_max
                )
            )
        }
    }

    private suspend fun replaceHourlyWeatherDataInDb() {
        weatherHourlyDao.deleteAll()

        var dayNumber = 1L
        for (weather in weatherHourlyDisplay!!) {
            val weatherHourly = WeatherHourly(
                id = dayNumber++,
                time = weather.time,
                weatherState = weather.weather_state,
                temperature = weather.temperature_2m.toFloat(), // Assuming the WeatherHourly class takes a Float
                relativeHumidity = weather.relative_humidity_2m,
                precipitationProbability = weather.precipitation_probability,
                windSpeed = weather.wind_speed_10m.toFloat(),
                windDirection = weather.wind_direction_10m
            )
            weatherHourlyDao.insert(weatherHourly)
        }
    }

    suspend fun insert(weather: Weather) {
        Log.d("TAG", weather.time)
        weatherDao.insert(weather)
    }

    fun getWeather(id: Long): Flow<Weather> = weatherDao.get(id)
    suspend fun searchPlaces(query: String): Flow<List<PlaceData>> = flow {
        val result = mutableListOf<PlaceData>()
        val response = WeatherApi.searchPlaces(query)
        if (response.isSuccessful) {
            response.body()?.let { places ->
                result.addAll(places)
            }
        }
        emit(result)
    }

    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ))
    }

    /**
     * optional. isnt used
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }


}