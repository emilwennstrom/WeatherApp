package algot.emil.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceDao {

    @Query("SELECT name FROM place WHERE place.id = 1")
    fun getName() : Flow<String>

    @Query("SELECT * FROM place where id = 1")
    fun getPlace() : Flow<Place>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: Place);

}