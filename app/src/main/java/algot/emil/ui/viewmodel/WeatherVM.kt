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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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


    private fun getWeatherFromDb() {
        viewModelScope.launch {
            launch {
                weatherModel.sevenDayWeather.collect { wList ->
                    _allWeather.value = wList // setting the list
                }
            }
            launch {
                weatherModel.getHourlyWeatherFromCurrentTimeFromDb().collect { wList ->
                    _allWeatherHourly.value = wList // setting the list
                }
            }
        }
    }

    fun updateWeatherFromQuery(placeData: PlaceData) {
        if (getConnectivity()) {
            viewModelScope.launch {
                val success =
                    weatherModel.fetchWeatherData(placeData.lat.toFloat(), placeData.lon.toFloat())
                if (success.first && success.second) {
                    placeRepository.insert(
                        place = Place(
                            name = placeData.display_name,
                            latitude = placeData.lat.toFloat(),
                            longitude = placeData.lon.toFloat()
                        )
                    )
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateHourly(time: String) {
        Log.d("updateHourly", "inside updateHourly")
        Log.d(TAG, time)
        var place: Place?
        viewModelScope.launch {
            placeRepository.getPlace().collect { currentPlace ->
                place = currentPlace
                val latitude = place?.latitude
                val longitude = place?.longitude
                if (latitude != null && longitude != null) {
                    weatherModel.fetchHourlyWeatherWithStartDate(
                        latitude, longitude, time
                    ).collect {
                        if (it.isNotEmpty()){
                            //_allWeatherHourly.value = emptyList()
                            _allWeatherHourly.value = it
                        }

                    }
                }
                else {
                    Log.d(TAG, "Null coordinates")
                }
            }
        }

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
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            ?: ""
    }

    init {

        getWeatherFromDb()
        getCurrentPlaceName()


        if (getConnectivity()) {
            viewModelScope.launch {
                placeRepository.getPlace().collect {
                    val current = it
                    if (current != null) {
                        weatherModel.fetchWeatherData(current.latitude, current.longitude)
                    }
                }
            }
        }
    }


}