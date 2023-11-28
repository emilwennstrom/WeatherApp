package algot.emil.ui.viewmodel

import algot.emil.PersistenceContext
import algot.emil.api.DailyUnits
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

    private val _temperatureUnit = MutableStateFlow<String>("C?") //for example, C (celsius) or F (fahrenheit)
    val temperatureUnit: StateFlow<String>
        get() = _temperatureUnit

    fun getWeatherNextSevenDays() {
        viewModelScope.launch { // launching a new coroutine
            if(weatherModel.fetchWeatherNextSevenDays()){
                if(weatherModel.weatherDisplay!=null){
                    _dailyWeather.value= weatherModel.weatherDisplay!!
                }
                if(weatherModel.temperatureUnit!=null){
                    _temperatureUnit.value= weatherModel.temperatureUnit!!
                }
            }
        }
    }
}