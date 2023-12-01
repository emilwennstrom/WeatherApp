package algot.emil.ui.viewmodel

import algot.emil.PersistenceContext
import algot.emil.data.PlaceData
import algot.emil.data.TopBarProperties
import algot.emil.model.PlaceRepository
import algot.emil.model.WeatherModel
import algot.emil.persistence.Place
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherHourly
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

private const val TAG = "WeatherVM"

interface WeatherViewModel {
    fun convertDateToWeekday(dateStr: String): String
}


@RequiresApi(Build.VERSION_CODES.O)
class WeatherVM(application: Application) : AndroidViewModel(application = application),
    WeatherViewModel {

    private val connectivity =
        application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val persistenceContext = application as PersistenceContext

    private val weatherModel: WeatherModel = WeatherModel(persistenceContext, connectivity)

    private val placeRepository: PlaceRepository = PlaceRepository(persistenceContext.placeDao)

    private val _topBarState = MutableStateFlow(TopBarProperties())
    val topBarState: StateFlow<TopBarProperties> = _topBarState.asStateFlow()

    private val _allWeatherHourly = MutableStateFlow(emptyList<WeatherHourly>())
    val allWeatherHourly = _allWeatherHourly.asStateFlow()

    private val _allWeather = MutableStateFlow(emptyList<Weather>())
    val allWeather = _allWeather.asStateFlow()

    private val _places = MutableStateFlow(emptyList<PlaceData>())
    val places = _places.asStateFlow()

    private val _currentDate = MutableStateFlow("")
    val currentDate = _currentDate.asStateFlow()

    fun getConnectivity(): Boolean {
        return weatherModel.isNetworkAvailable()
    }

    fun showSearch() {
        _topBarState.value =
            topBarState.value.copy(isSearchShown = !topBarState.value.isSearchShown)
    }


    private fun getCurrentPlaceName() {
        viewModelScope.launch {
            placeRepository.getName().collect {
                _topBarState.value = topBarState.value.copy(currentPlace = it)
            }
        }
    }

    fun updateWeatherFromQuery(placeData: PlaceData) {
        if (getConnectivity()) {
            viewModelScope.launch {
                val lat = placeData.lat.toFloat()
                val lon = placeData.lon.toFloat()
                launch {
                    weatherModel.fetchWeatherNextSevenDays(
                        lat,
                        lon
                    ).collect {
                        _allWeather.value = it
                    }
                }
                launch {
                    weatherModel.fetchHourlyWeatherWithStartDate(
                        lat,
                        lon,
                        LocalDate.now().toString()
                    )
                        .collect {
                            _allWeatherHourly.value = it
                        }
                }
                launch {
                    placeRepository.insert(
                        place = Place(
                            name = placeData.display_name,
                            latitude = lat,
                            longitude = lon
                        )
                    )
                }

            }
            updateTopBarTextField("")
            _topBarState.value =
                topBarState.value.copy(isSearchShown = false, currentPlace = placeData.display_name)
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateHourly(time: String) {
        var place: Place?
        viewModelScope.launch {
            placeRepository.getPlace().collect { currentPlace ->
                place = currentPlace
                val latitude = place?.latitude
                val longitude = place?.longitude
                if (latitude != null && longitude != null) {
                    launch {
                        weatherModel.fetchHourlyWeatherWithStartDate(
                            latitude, longitude, time
                        ).collect {
                            if (it.isNotEmpty()){
                                _allWeatherHourly.value = it
                                val dateTime = LocalDateTime.parse(it[0].time)
                                _currentDate.value = dateTime.toLocalDate().toString()
                            }

                        }
                    }
                } else {
                    Log.d(TAG, "Null coordinates")
                }
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
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            ?: ""
    }

    init {
        getCurrentPlaceName()
        viewModelScope.launch {
            try {
                if (getConnectivity()) {
                   val currentPlace = placeRepository.getPlace().firstOrNull()
                    if (getConnectivity() && currentPlace != null) {
                        val lat = currentPlace.latitude
                        val lon = currentPlace.longitude
                       launch {
                            weatherModel.fetchHourlyWeatherWithStartDate(lat, lon, LocalDate.now().toString()).collect {
                                _allWeatherHourly.value = it
                            }
                        }
                        launch {
                            weatherModel.fetchWeatherNextSevenDays(lat, lon).collect {
                                _allWeather.value = it
                            }
                        }
                    }
                } else {
                    launch {
                        weatherModel.sevenDayWeather.collect { weatherList ->
                            _allWeather.value = weatherList
                        }
                    }
                    launch {
                        weatherModel.getHourlyWeatherFromCurrentTimeFromDb()
                            .collect { weatherList -> _allWeatherHourly.value = weatherList }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
            launch {
                val weather = weatherModel.getSavedWeatherDate().firstOrNull()
                if (weather != null) {
                    val dateTime = LocalDateTime.parse(weather.time)
                    _currentDate.value = dateTime.toLocalDate().toString()
                }

            }

        }
    }


}