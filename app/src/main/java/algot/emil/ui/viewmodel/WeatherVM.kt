package algot.emil.ui.viewmodel

import algot.emil.api.WeatherApi
import algot.emil.api.RetrofitHelper
import algot.emil.model.WeatherModel
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface WeatherViewModel{

}

class WeatherVM() : ViewModel(), WeatherViewModel {

    private val weatherModel: WeatherModel = WeatherModel() // Skapa en instans av WeatherModel
    private val _name = MutableStateFlow("Algot")
    val name: StateFlow<String>
        get() = _name

    public fun getWeatherNextSevenDays(){
        Log.d("GetWeatherResults: ", "inside getWeatherNextSevenDays")
        val weatherApi = RetrofitHelper.getInstance().create(WeatherApi::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            Log.d("GetWeatherResults: ", "starting API call")
            val result = weatherApi.getQuotes()
            if (result != null)
                // Checking the results
                Log.d("GetWeatherResults: ", result.body().toString())
            else{
                Log.d("GetWeatherResults:", result)
            }
        }
    }





}