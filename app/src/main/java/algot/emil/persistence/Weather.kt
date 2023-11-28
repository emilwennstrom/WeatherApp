package algot.emil.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Weather(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "temperature_2m_max") val temperature: Float,
    @ColumnInfo(name = "rain_sum") val rainSum: Float,
    @ColumnInfo(name = "showers_sum") val showersSum: Float,
    @ColumnInfo(name = "snowfall_sum") val snowfallSum: Float
)