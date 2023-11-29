package algot.emil.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceDao {

    @Query("SELECT name FROM place WHERE place.id = 1")
    fun get() : Flow<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: Place);

}