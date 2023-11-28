package algot.emil.persistence

import algot.emil.enums.WeatherState
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Weather(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "weather_state") val weatherState: WeatherState,
    @ColumnInfo(name = "temperature_2m_max") val temperature: Float,
)