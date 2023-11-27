package algot.emil.persistence
import androidx.lifecycle.LiveData
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
    suspend fun insert(weather: Weather): Long

    @Delete
    suspend fun delete(weather: Weather)
}