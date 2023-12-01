package algot.emil.persistence

import algot.emil.enums.WeatherState
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherHourly (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "weather_state") val weatherState: WeatherState,
    @ColumnInfo(name = "temperature_2m") val temperature: Float, //laddas in som double
    @ColumnInfo(name = "relative_humidity_2m") val  relativeHumidity: Int,
    @ColumnInfo(name = "precipitation_probability") val  precipitationProbability: Int,
    @ColumnInfo(name = "wind_speed_10m") val  windSpeed: Float,
    @ColumnInfo(name = "wind_direction_10m") val  windDirection: Int,
)