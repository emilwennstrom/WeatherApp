package algot.emil.ui.viewmodel

import algot.emil.api.RetrofitHelper
import algot.emil.api.WeatherApi
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

    fun getWeatherNextSevenDays() {
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