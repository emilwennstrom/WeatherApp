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
