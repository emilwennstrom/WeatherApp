package algot.emil.ui.viewmodel

import algot.emil.PersistenceContext
import algot.emil.api.DailyWeatherDisplay
import algot.emil.data.PlaceData
import algot.emil.data.TopBarProperties
import algot.emil.enums.WeatherState
import algot.emil.model.PlaceRepository
import algot.emil.model.WeatherModel
import algot.emil.persistence.Place
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherHourly
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val placeRepository: PlaceRepository = PlaceRepository(persistenceContext.placeDao)

    private val _name = MutableStateFlow("Algot")

    private val _dayOfWeek = MutableStateFlow<Weather?>(null)
    val dayOfWeek: StateFlow<Weather?> = _dayOfWeek.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _topBarState = MutableStateFlow(TopBarProperties())
    val topBarState: StateFlow<TopBarProperties> = _topBarState.asStateFlow()

    private val _allWeatherHourly = MutableStateFlow(emptyList<WeatherHourly>())
    val allWeatherHourly = _allWeatherHourly.asStateFlow()

    private val _allWeather = MutableStateFlow(emptyList<Weather>())
    val allWeather = _allWeather.asStateFlow()

    private val _places = MutableStateFlow(emptyList<PlaceData>())
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


    fun getConnectivity() : Boolean {
        return weatherModel.isNetworkAvailable()
    }

    fun showSearch() {
        _topBarState.value = topBarState.value.copy(isSearchShown = !topBarState.value.isSearchShown)
        Log.d(TAG, "TopBarState is now: " + _topBarState.value.isSearchShown)
    }


    private fun getCurrentPlaceName(){
        viewModelScope.launch {
            placeRepository.getName().collect {
                _topBarState.value = topBarState.value.copy(currentPlace = it)
            }
        }
    }



    private fun getWeatherFromDb() {
        viewModelScope.launch {
            launch {
                weatherModel.allWeather.collect {
                        wList ->
                    _allWeather.value = wList // setting the list
                }
            }
            launch {
                weatherModel.getAllWeatherHourlyFromTime()
                weatherModel.allWeatherHourlyFromTime.collect {
                        wList ->
                    _allWeatherHourly.value = wList // setting the list
                }
            }
        }
        _isLoading.value = false
    }

    fun updateWeatherFromQuery(placeData: PlaceData) {
        if (getConnectivity()) {
            viewModelScope.launch {
                val success = weatherModel.fetchWeatherData(placeData.lat.toFloat(), placeData.lon.toFloat())
                if (success.first && success.second) {
                    _isLoading.value = false
                    placeRepository.insert(place = Place(name = placeData.display_name, latitude = placeData.lat.toFloat(), longitude = placeData.lon.toFloat()))
                }
            }
            updateTopBarTextField("")
            _topBarState.value = topBarState.value.copy(isSearchShown = false)
        }
    }

    fun updateTopBarTextField(text: String) {
        _topBarState.value = topBarState.value.copy(searchText = text)
    }

    fun onSearchTextChanged(query: String) {
        updateTopBarTextField(query)
        if (getConnectivity()) {
            viewModelScope.launch {
                if (query.isNotEmpty()) {
                    weatherModel.searchPlaces(query).collect { placeList ->
                        _places.value = placeList
                    }

                }

            }
        } else {
            _places.value = emptyList()
        }
    }

    //TODO: anropa placeRepository.get() för att få longitud och latitud.
    //TODO: Sedan anropa eatherModel.fetchWeatherNextHoursWithStartDate med longitud, latitud och reformatedTime
    //TODO: då borde hourly uppdateras för den dag man klickar på.
    fun updateHourly(time: String){
        var reformatedTime = weatherModel.reformatDate(time)
        //placeRepository.get()
        //weatherModel.fetchWeatherNextHoursWithStartDate(reformatedTime)
        //weatherModel.allWeatherHourlyFromTime(reformatedTime)
        //launch {
        //    weatherModel.allWeatherHourlyFromTime(reformatedTime)
        //    weatherModel.allWeatherHourlyFromTime.collect {
        //            wList ->
        //        _allWeatherHourly.value = wList // setting the list
        //    }
        //}
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
        getCurrentPlaceName()

    }

}