package algot.emil.ui.viewmodel

import algot.emil.api.QuotesApi
import algot.emil.api.RetrofitHelper
import algot.emil.model.WeatherModel
import android.content.Context
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
        val quotesApi = RetrofitHelper.getInstance().create(QuotesApi::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            Log.d("GetWeatherResults: ", "starting API call")
            val result = quotesApi.getQuotes()
            if (result != null)
                // Checking the results
                Log.d("GetWeatherResults: ", result.body().toString())
            else{
                Log.d("GetWeatherResults:", result)
            }
        }
    }





}