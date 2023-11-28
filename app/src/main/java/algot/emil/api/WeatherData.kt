package algot.emil.api

import algot.emil.enums.WeatherState


data class WeatherData(
    val latitude: Float,
    val longitude: Float,
    val generationtime_ms: Float,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Float,
    val daily_units: DailyUnits,
    val daily: DailyWeather
)

data class DailyUnits(
    val time: String,
    val weather_code: String,
    val temperature_2m_max: String
)

data class DailyWeather(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Float>
)

/**
 * data class used for displaying the data in view-model.
 */
data class DailyWeatherDisplay(
    val time: String,
    val weather_State_code: WeatherState,
    val temperature_2m_max: Float
)

data class HourlyWeatherData(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val hourly_units: HourlyUnits,
    val hourly: HourlyData
)

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val precipitation_probability: String,
    val weather_code: String,
    val wind_speed_10m: String,
    val wind_direction_10m: String
)

data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val relative_humidity_2m: List<Int>,
    val precipitation_probability: List<Int>,
    val weather_code: List<Int>,
    val wind_speed_10m: List<Double>,
    val wind_direction_10m: List<Int>
)

data class HourlyDataDisplay(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val precipitation_probability: Int,
    val weather_state: WeatherState,
    val wind_speed_10m: Double,
    val wind_direction_10m: Int
)
