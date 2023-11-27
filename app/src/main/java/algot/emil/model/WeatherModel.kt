package algot.emil.model

import algot.emil.persistence.AppDatabase
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherRepository
import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WeatherModel(context: Context) {
    private val database by lazy { AppDatabase.getDatabase(context) }
    private val weatherRepository by lazy { WeatherRepository(database.weatherDao()) }

    val allWeather: Flow<List<Weather>> = weatherRepository.allWeather


    @OptIn(DelicateCoroutinesApi::class)
    fun insert(weather: Weather) = GlobalScope.launch {
        weatherRepository.insert(weather)
    }



}