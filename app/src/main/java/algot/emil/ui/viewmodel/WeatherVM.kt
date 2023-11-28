package algot.emil.ui.viewmodel

import algot.emil.PersistenceContext
import algot.emil.api.RetrofitHelper
import algot.emil.api.WeatherApi
import algot.emil.model.WeatherModel
import algot.emil.persistence.Weather
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun getWeatherNextSevenDays() {
        viewModelScope.launch {
            weatherModel.insert(weather = Weather(1, "a", 1F, 2F, 3F, 4F,))
        }
        Log.d("GetWeatherResults: ", "inside getWeatherNextSevenDays")
        val weatherApi = RetrofitHelper.getInstance().create(WeatherApi::class.java)
        // launching a new coroutine
        viewModelScope.launch {
            Log.d("GetWeatherResults: ", "starting API call")
            val result = weatherApi.getQuotes()
            Log.d("GetWeatherResults: ", result.body().toString())
        }

    }





}