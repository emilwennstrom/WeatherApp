package algot.emil.api


data class WeatherData2(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val daily_units: Map<String, String>, // Map to handle the units
    val daily: DailyWeatherData2
)

data class DailyWeatherData2(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val rain_sum: List<Double>,
    val showers_sum: List<Double>,
    val snowfall_sum: List<Double>
)

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
