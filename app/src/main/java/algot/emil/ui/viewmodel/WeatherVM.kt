package algot.emil.ui.viewmodel

import algot.emil.PersistenceContext
import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.PlaceData
import algot.emil.enums.WeatherState
import algot.emil.model.WeatherModel
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherHourly
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "WeatherVM"

interface WeatherViewModel{
    fun convertDateToWeekday(dateStr: String): String
}


class WeatherVM(application: Application) : AndroidViewModel(application = application),
    WeatherViewModel {

    private val connectivity = application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val persistenceContext = application as PersistenceContext

    private val weatherModel: WeatherModel =
        WeatherModel(persistenceContext, connectivity) // Skapa en instans av WeatherModel

    private val _name = MutableStateFlow("Algot")

    private val _dayOfWeek = MutableStateFlow<Weather?>(null)
    val dayOfWeek: StateFlow<Weather?> = _dayOfWeek.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()



    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val somePlaceData = listOf(
        PlaceData(
            display_name = "Stockholm",
            lat = "59",
            lon = "18"
        )

    )

    private val _allWeatherHourly = MutableStateFlow(emptyList<WeatherHourly>())
    val allWeatherHourly = _allWeatherHourly.asStateFlow()

    private val _allWeather = MutableStateFlow(emptyList<Weather>())
    val allWeather = _allWeather.asStateFlow()

    private val _places = MutableStateFlow(somePlaceData)
    val places = _places.asStateFlow()


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val name: StateFlow<String>
        get() = _name

    private val _dailyWeather = MutableStateFlow<List<DailyWeatherDisplay>>(
        listOf(
            DailyWeatherDisplay(
                time = "2023-11-28",
                weather_State_code = WeatherState.ClearSky,
                temperature_2m_max = 0.0F // Default temperature
            )
            // Add more DailyWeatherDisplay objects as needed
        )
    )
    val dailyWeather: StateFlow<List<DailyWeatherDisplay>> //what weather-information to display from today to 7 days forward with daily updates
        get() = _dailyWeather

    private val _temperatureUnit =
        MutableStateFlow<String>("C?") //for example, C (celsius) or F (fahrenheit)
    val temperatureUnit: StateFlow<String>
        get() = _temperatureUnit

    /*fun getWeatherNextSevenDays() {
        viewModelScope.launch { // launching a new coroutine
            if (weatherModel.fetchWeatherNextSevenDays()) {
                if (weatherModel.weatherDisplay != null) {
                    _dailyWeather.value = weatherModel.weatherDisplay!!
                }
                if (weatherModel.temperatureUnit != null) {
                    _temperatureUnit.value = weatherModel.temperatureUnit!!
                }
                _isLoading.value = false
            }
        }
    }*/

   /* fun getWeatherHourly() {
        viewModelScope.launch { // launching a new coroutine
            if (weatherModel.fetchWeatherNextHours()) {
                //TODO: implement
            }
        }
    }*/

    fun getWeatherFromDb() {
        viewModelScope.launch {
            launch {
                weatherModel.allWeather.collect {
                        wList ->
                    _allWeather.value = wList // setting the list
                }
            }
            launch {
                weatherModel.allWeatherHourly.collectLatest {
                        wList -> _allWeatherHourly.value = wList
                }
            }
        }
        _isLoading.value = false
    }

    fun updateWeatherFromQuery(placeData: PlaceData) {
        Log.d(TAG, "Lat: " + placeData.lat + " Lon: " + placeData.lon)


        viewModelScope.launch {
            val success = weatherModel.fetchWeatherData(placeData.lat.toFloat(), placeData.lon.toFloat())
            if (success.first && success.second) {
                _isLoading.value = false
            }
        }

        Log.d(TAG, placeData.display_name)
    }

    fun onSearchTextChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            delay(1000)
            if (query.isNotEmpty()) {
                weatherModel.searchPlaces(query).collect { placeList ->
                    _places.value = placeList
                }

            }

        }
    }

    fun loadDayOfWeek(dayOfWeek: Int) {
        viewModelScope.launch {
            weatherModel.getWeather(dayOfWeek.toLong()).collect { weather ->
                _dayOfWeek.value = weather
            }
        }

    }

    override fun convertDateToWeekday(dateStr: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateStr)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""
    }

    init {

        getWeatherFromDb()

    }

}