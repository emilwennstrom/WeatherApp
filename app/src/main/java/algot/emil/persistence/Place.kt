package algot.emil.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Place (

    @PrimaryKey(autoGenerate = false) val id: Long = 1,
    @ColumnInfo(name = "name") val name: String

)