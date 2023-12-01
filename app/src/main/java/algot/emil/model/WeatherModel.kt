package algot.emil.model

import algot.emil.PersistenceContext
import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.HourlyDataDisplay
import algot.emil.api.WeatherApi
import algot.emil.api.WeatherConverter
import algot.emil.data.PlaceData
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherHourly
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale


private const val TAG = "WeatherModel"

class WeatherModel(persistenceContext: PersistenceContext, connectivity: ConnectivityManager) {
    private val weatherDao = persistenceContext.weatherDao
    val sevenDayWeather = weatherDao.getAll()
    private val weatherHourlyDao = persistenceContext.weatherHourlyDao

    private val connectivityManager = connectivity


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHourlyWeatherFromCurrentTimeFromDb(): Flow<List<WeatherHourly>> {
        return weatherHourlyDao.getFirst()
            .take(1)
            .flatMapConcat { weatherHourly ->
                weatherHourlyDao.getAllAfter(weatherHourly.time, addOneDay(weatherHourly.time))
            }
    }

    private fun getCurrentDateTimePlus24HoursFormatted(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        return format.format(calendar.time)
    }

    /**
     * decreases current hour by -1 to be able to retreive WeatherHourly during current hour.
     */
    private fun getCurrentDateTimeFormatted(): String {
        val calendar = Calendar.getInstance()
        // Check if the hour is 0 and set to 23, else decrease by 1
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            calendar.set(
                Calendar.HOUR_OF_DAY, 23
            ) //tror detta är onödigt. Detta lär skötas automatiskt av -1 nedan ändå.
        } else {
            calendar.add(Calendar.HOUR_OF_DAY, -1)
        }
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        return format.format(calendar.time)
    }

    suspend fun fetchWeatherNextSevenDays(lat: Float, lon: Float): Flow<List<Weather>> {
        if (isNetworkAvailable()) {
            val result = WeatherApi.getDailyWeatherForSevenDays(lat, lon)
            if (result.isSuccessful && result.body() != null) {
                val resultBody = result.body()!!  // Extract WeatherData from the response
                val weatherDisplay = WeatherConverter().getDailyWeatherDisplay(resultBody)
                replaceWeatherDataInDb(weatherDisplay)
                return weatherDao.getAll()
            }
        }
        return emptyFlow()
    }

    /**
     * is used to calculate end-date for API-calls, with start-date as input.
     */
    private fun addOneDay(dateStr: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateStr)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        Log.d("AddOneDay", calendar.time.toString())
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        Log.d("updateHourly", "endDate: " + format.format(calendar.time))
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
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchHourlyWeatherWithStartDate(
        lat: Float, lon: Float, startDate: String
    ): Flow<List<WeatherHourly>> {

        if (!isNetworkAvailable()) return emptyFlow()
        val endDate = addOneDay(startDate)
        val result = WeatherApi.getHourlyWeatherWithTimeInterval(
            lat, lon, startDate, endDate
        )
        if (!result.isSuccessful || result.body() == null) {
            return emptyFlow()
        }
        val resultBody = result.body()!!  // Extract WeatherData from the response
        val weatherHourlyDisplay = WeatherConverter().getHourlyWeatherDisplay(resultBody)
        val weatherHourly = apiDataToWeatherHourly(weatherHourlyDisplay)

        var display = mutableListOf<WeatherHourly>()
        display = if (LocalDate.now().toString() == startDate) {
            getHourlyWeatherOfToday(weatherHourly)
        } else {
            getHourlyWeatherOfOtherDay(weatherHourly, startDate)
        }

        replaceHourlyDataInDb(display)

        return weatherHourlyDao.getAll()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHourlyWeatherOfOtherDay(
        weatherHourly: MutableList<WeatherHourly>, startDate: String
    ): MutableList<WeatherHourly> {
        val newList: MutableList<WeatherHourly> = mutableListOf()
        for (element in weatherHourly) {
            val dateTime = LocalDateTime.parse(element.time)
            val date = dateTime.toLocalDate()
            if (date == LocalDate.parse(startDate)) {
                newList.add(element)
            }
        }
        return newList
    }

    private suspend fun replaceHourlyDataInDb(display: MutableList<WeatherHourly>) {
        weatherHourlyDao.deleteAll()
        weatherHourlyDao.insertAll(display)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHourlyWeatherOfToday(weatherHourly: MutableList<WeatherHourly>): MutableList<WeatherHourly> {
        val newList: MutableList<WeatherHourly> = mutableListOf()
        for (element in weatherHourly) {
            val dateTime = LocalDateTime.parse(element.time)
            val date = dateTime.toLocalDate()
            if (dateTime >= LocalDateTime.now() && date == LocalDate.now()) {
                newList.add(element)
            }

        }
        return newList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchWeatherNextHours(lat: Float, lon: Float): Boolean {
        fetchHourlyWeatherWithStartDate(lat, lon, getCurrentDate())
        return true
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }


    private suspend fun replaceWeatherDataInDb(weatherDisplay: List<DailyWeatherDisplay>) {
        //weatherDao.deleteAll()
        val weatherList: MutableList<Weather> = mutableListOf()
        var dayNumber = 1L
        for (weather in weatherDisplay) {
            val weatherData = Weather(
                id = dayNumber++,
                time = weather.time,
                weatherState = weather.weather_State_code,
                temperature = weather.temperature_2m_max
            )
            weatherList.add(weatherData)
        }
        weatherDao.insertAll(weatherList)
    }

    private fun apiDataToWeatherHourly(weatherHourlyDisplay: List<HourlyDataDisplay>): MutableList<WeatherHourly> {
        val weatherHourlyList: MutableList<WeatherHourly> = mutableListOf()
        for (weather in weatherHourlyDisplay) {
            val weatherHourly = WeatherHourly(
                time = weather.time,
                weatherState = weather.weather_state,
                temperature = weather.temperature_2m.toFloat(), // Assuming the WeatherHourly class takes a Float
                relativeHumidity = weather.relative_humidity_2m,
                precipitationProbability = weather.precipitation_probability,
                windSpeed = weather.wind_speed_10m.toFloat(),
                windDirection = weather.wind_direction_10m
            )
            weatherHourlyList.add(weatherHourly)
        }
        return weatherHourlyList
    }

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