package algot.emil.ui.viewmodel

import algot.emil.api.DailyWeatherDisplay
import algot.emil.api.RetrofitHelper
import algot.emil.api.WeatherConverter
import algot.emil.api.WeatherApi
import algot.emil.enums.Weather
import algot.emil.model.WeatherModel
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface WeatherViewModel


class WeatherVM(application: Application) : AndroidViewModel(application = application),
    WeatherViewModel {


    @SuppressLint("StaticFieldLeak")
    private val applicationContext = getApplication<Application>().applicationContext
    private val weatherModel: WeatherModel =
        WeatherModel(applicationContext) // Skapa en instans av WeatherModel
    private val _name = MutableStateFlow("Algot")
    val name: StateFlow<String>
        get() = _name

    private val _dailyWeather = MutableStateFlow(DailyWeatherDisplay(
        time = "2023-11-28", // Default value
        weather_code = Weather.ClearSky, // Default Weather value
        temperature_2m_max = 0.0 // Default temperature
    ))
    val dailyWeather: StateFlow<DailyWeatherDisplay>
        get() = _dailyWeather

    fun getWeatherNextSevenDays() {
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
                    Log.d("GetWeatherResults:", "list of result converted: "+ weatherDisplay.toString())
                    val displayUnit = WeatherConverter().getDailyUnits(resultBody)
                    Log.d("GetWeatherResults:", "daily units: "+ displayUnit.toString())
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