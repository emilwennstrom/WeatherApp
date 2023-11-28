package algot.emil.ui.viewmodel

import algot.emil.PersistenceContext
import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.RetrofitHelper
import algot.emil.api.WeatherConverter
import algot.emil.api.WeatherApi
import algot.emil.enums.WeatherState
import algot.emil.model.WeatherModel
import algot.emil.persistence.Weather
import android.app.Application
import android.util.Log
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
        viewModelScope.launch {
            weatherModel.insert(weather = Weather(time = "Now", weatherState = WeatherState.ClearSky, temperature = 1.1F))
        }
        Log.d("GetWeatherResults: ", "inside getWeatherNextSevenDays")
        val weatherApi = RetrofitHelper.getInstance().create(WeatherApi::class.java)
        // launching a new coroutine
        viewModelScope.launch {
            Log.d("GetWeatherResults: ", "starting API call")
            val result = weatherApi.getDailyWeatherForSevenDays()
            if (result != null){
                // Checking the results
                Log.d("GetWeatherResults: ", result.body().toString())
                if (result.isSuccessful && result.body() != null) {
                    val resultBody = result.body()!!  // Extract WeatherData from the response
                    val weatherDisplay = WeatherConverter().getDailyWeatherDisplay(resultBody)
                    Log.d("GetWeatherResults:", "list of result converted: $weatherDisplay")
                    val displayUnit = WeatherConverter().getDailyUnits(resultBody)
                    Log.d("GetWeatherResults:", "daily units: $displayUnit")
                } else {
                    // Handle unsuccessful response or null body
                }
            }
            else{
                Log.d("GetWeatherResults:", result)
            }
            Log.d("GetWeatherResults: ", result.body().toString())
        }

    }
}