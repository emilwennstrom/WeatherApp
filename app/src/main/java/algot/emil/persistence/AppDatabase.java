package algot.emil.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    //public abstract UserDao userDao();
}
