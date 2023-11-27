package algot.emil.model;

import android.content.Context;

import algot.emil.persistence.AppDatabase;

public class WeatherModel {


    private final AppDatabase db = null;

    public WeatherModel() {

        /* Exempel på db hantering. context måste komma från vymodellen
        db = Room.databaseBuilder(applicationContext, AppDatabase.class, "WeatherDb").build();
        WeatherDao weatherDao = db.weatherDao();
        List<Weather> weatherList = weatherDao.getAll();
         */

    }


}
