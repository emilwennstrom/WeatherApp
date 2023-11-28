package algot.emil.api

import algot.emil.enums.WeatherState


data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
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
    val temperature_2m_max: List<Double>
)

/**
 * data class used for displaying the data in view-model.
 */
data class DailyWeatherDisplay(
    val time: String,
    val weather_State_code: WeatherState,
    val temperature_2m_max: Double
)
