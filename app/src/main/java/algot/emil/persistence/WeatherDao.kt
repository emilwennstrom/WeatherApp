package algot.emil.persistence
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query(value = "SELECT * FROM weather")
    fun getAll(): Flow<List<Weather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weatherList: List<Weather>)

    @Query(value = "SELECT * FROM weather WHERE id = :id")
    fun get(id: Long) : Flow<Weather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: Weather): Long

    @Query(value = "DELETE FROM weather")
    suspend fun deleteAll();

    @Delete
    suspend fun delete(weather: Weather)
}