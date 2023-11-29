package algot.emil

import algot.emil.persistence.AppDatabase
import algot.emil.persistence.PlaceDao
import algot.emil.persistence.WeatherDao
import algot.emil.persistence.WeatherHourlyDao
import android.app.Application

class PersistenceContext : Application() {

    private lateinit var database: AppDatabase
    lateinit var weatherDao: WeatherDao
    lateinit var weatherHourlyDao: WeatherHourlyDao
    lateinit var placeDao: PlaceDao
    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getDatabase(this)
        weatherDao = database.weatherDao()
        weatherHourlyDao = database.weatherHourlyDao()
        placeDao = database.placeDao()

    }

}