package algot.emil

import algot.emil.persistence.AppDatabase
import algot.emil.persistence.WeatherDao
import android.app.Application

class PersistenceContext : Application() {

    private lateinit var database: AppDatabase
    lateinit var weatherDao: WeatherDao
    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getDatabase(this)
        weatherDao = database.weatherDao()

    }



}