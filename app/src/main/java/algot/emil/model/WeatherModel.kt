package algot.emil.model

import algot.emil.PersistenceContext
import algot.emil.persistence.Weather
import android.util.Log

class WeatherModel(persistenceContext: PersistenceContext) {
    private val weatherDao = persistenceContext.weatherDao

    val allWeather = weatherDao.getAll()


    suspend fun insert(weather: Weather){
        Log.d("TAG", weather.time)
        weatherDao.insert(weather)
    }


}