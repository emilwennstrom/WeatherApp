package algot.emil.model

import algot.emil.PersistenceContext
import algot.emil.api.DailyUnits
import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.HourlyDataDisplay
import algot.emil.api.HourlyUnits
import algot.emil.data.PlaceData
import algot.emil.api.WeatherApi
import algot.emil.api.WeatherConverter
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherHourly
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class WeatherModel(persistenceContext: PersistenceContext, connectivity: ConnectivityManager) {
    private val weatherDao = persistenceContext.weatherDao
    val allWeather = weatherDao.getAll()
    private val weatherHourlyDao = persistenceContext.weatherHourlyDao
    val allWeatherHourly = weatherHourlyDao.getAll()
    var allWeatherHourlyFromTime:  Flow<List<WeatherHourly>> = emptyFlow()

    private val connectivityManager = connectivity

    var weatherDisplay: List<DailyWeatherDisplay>? = null
    var displayUnit: DailyUnits? = null
    var temperatureUnit: String? = "C"


    var weatherHourlyDisplay: List<HourlyDataDisplay>? = null
    var hourlyUnits: HourlyUnits? = null

    public suspend fun getAllWeatherHourlyFromTime() {
        var startTime= getCurrentDateTimeFormatted()
        var endTime = getCurrentDateTimePlus24HoursFormatted()
        Log.d("WeatherModel", "date:" + getCurrentDateTimeFormatted())
        allWeatherHourlyFromTime = weatherHourlyDao.getAllAfter(startTime, endTime)
    }

    public suspend fun getAllWeatherHourly() {
        allWeatherHourlyFromTime = weatherHourlyDao.getAll()
    }

    fun getCurrentDateTimePlus24HoursFormatted():String{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        return format.format(calendar.time)
    }

    /**
     * decreases current hour by -1 to be able to retreive WeatherHourly during current hour.
     */
    fun getCurrentDateTimeFormatted(): String {
        val calendar = Calendar.getInstance()
        // Check if the hour is 0 and set to 23, else decrease by 1
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            calendar.set(Calendar.HOUR_OF_DAY, 23) //tror detta är onödigt. Detta lär skötas automatiskt av -1 nedan ändå.
        } else {
            calendar.add(Calendar.HOUR_OF_DAY, -1)
        }
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        return format.format(calendar.time)
    }

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

    /**
     * is used to calculate end-date for API-calls, with start-date as input.
     */
    fun addOneDay(dateStr: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateStr)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        Log.d("updateHourly", "endDate: " +format.format(calendar.time))
        return format.format(calendar.time)
    }


    /**
     * Usage example:
     * val reformattedDate = reformatDate("2023-11-30T14:00") // Returns "2023-11-30"
     *
     */
    fun reformatDate(dateStr: String): String {
        return dateStr.split("T")[0]
    }


    /**
     * Note: format for startDate is "2023-11-30"
     */
    public suspend fun fetchWeatherNextHoursWithStartDate(lat: Float, lon: Float, startDate:String): Boolean {
        if(isNetworkAvailable()){
            Log.d("GetWeatherResultsHourly: ", "starting API call")
            val result = WeatherApi.getHourlyWeatherWithTimeInterval(lat, lon, startDate,addOneDay(startDate) )
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
                replaceHourlyWeatherDataInDb()
                return true
            }
            return false
        }else{
            weatherHourlyDao.getAll()
            return true
        }

    }


    private suspend fun fetchWeatherNextHours(lat: Float, lon: Float): Boolean {
        println("GetWeatherResultsHourly")
        Log.d("GetWeatherResultsHourly", "inside fetchWeatherNextHours")
        Log.d("GetWeatherResultsHourly", "getCurrentDate: " + getCurrentDate())
        return fetchWeatherNextHoursWithStartDate(lat,lon,getCurrentDate())
    }
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
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
            Log.d("replaceHourlyWeatherDataInDb", weatherHourly.toString())
        }
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
}