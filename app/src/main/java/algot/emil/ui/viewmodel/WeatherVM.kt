package algot.emil.ui.viewmodel

import algot.emil.PersistenceContext
import algot.emil.api.DailyWeatherDisplay
import algot.emil.enums.WeatherState
import algot.emil.model.WeatherModel
import algot.emil.persistence.Weather
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface WeatherViewModel


class WeatherVM(application: Application) : AndroidViewModel(application = application),
    WeatherViewModel {

    private val persistenceContext = application as PersistenceContext
    private val weatherModel: WeatherModel =
        WeatherModel(persistenceContext) // Skapa en instans av WeatherModel
    private val _name = MutableStateFlow("Algot")

    val allWeather: Flow<List<Weather>> = weatherModel.allWeather

    val name: StateFlow<String>
        get() = _name

    private val _dailyWeather = MutableStateFlow(DailyWeatherDisplay(
        time = "2023-11-28",
        weather_State_code = WeatherState.ClearSky,
        temperature_2m_max = 0.0F // Default temperature
    ))
    val dailyWeather: StateFlow<DailyWeatherDisplay>
        get() = _dailyWeather

    fun getWeatherNextSevenDays() {
        viewModelScope.launch { // launching a new coroutine
            if(weatherModel.fetchWeatherNextSevenDays()){
                weatherModel.weatherDisplay
                weatherModel.displayUnit
            }
        }
    }
}