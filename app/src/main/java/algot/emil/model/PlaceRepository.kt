package algot.emil.model

import algot.emil.persistence.Place
import algot.emil.persistence.PlaceDao
import kotlinx.coroutines.flow.Flow

class PlaceRepository(private val placeDao: PlaceDao) {


    suspend fun insert(placeName: String){
        placeDao.insert(Place(name = placeName))
    }


    suspend fun get() : Flow<String> {
        return placeDao.get()
    }


}