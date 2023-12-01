package algot.emil.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherHourlyDao {

    @Query(value = "SELECT * FROM WeatherHourly")
    fun getAll(): Flow<List<WeatherHourly>>

    @Query("SELECT * FROM WeatherHourly WHERE time > :startTime AND time < :endTime" )
    fun getAllAfter(startTime: String, endTime: String): Flow<List<WeatherHourly>>

    @Query(value = "SELECT * FROM WeatherHourly WHERE id = :id")
    fun get(id: Long) : Flow<WeatherHourly>

    @Query("SELECT * FROM WeatherHourly LIMIT 1")
    fun getFirst() : Flow<WeatherHourly>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherHourly: WeatherHourly): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weatherHourly: List<WeatherHourly>)

    @Query(value = "DELETE FROM WeatherHourly")
    suspend fun deleteAll();

    @Delete
    suspend fun delete(weatherHourly: WeatherHourly)
}