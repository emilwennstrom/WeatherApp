package algot.emil.model

import algot.emil.persistence.Place
import algot.emil.persistence.PlaceDao
import kotlinx.coroutines.flow.Flow

class PlaceRepository(private val placeDao: PlaceDao) {


    suspend fun insert(place: Place){
        placeDao.insert(place)
    }

    fun getPlace() : Flow<Place> {
        return placeDao.getPlace()
    }

    fun getName() : Flow<String> {
        return placeDao.getName()
    }


}