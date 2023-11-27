package algot.emil.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow


class WeatherRepository(private val weatherDao : WeatherDao) {

    val allWeather: Flow<List<Weather>> = weatherDao.getAll()



    @WorkerThread
    suspend fun insert(weather: Weather){
        weatherDao.insert(weather)
    }


}