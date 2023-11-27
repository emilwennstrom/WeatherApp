package algot.emil.persistence;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM weather")
    List<Weather> getAll();

    @Insert
    void insertAll(List<Weather> weatherList);



}
