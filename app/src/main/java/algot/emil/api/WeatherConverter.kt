package algot.emil.api

import algot.emil.enums.WeatherState

/*
   WMO Weather interpretation codes (WW)
   Code	Description
   0	Clear sky
   1, 2, 3	Mainly clear, partly cloudy, and overcast
   45, 48	Fog and depositing rime fog
   51, 53, 55	Drizzle: Light, moderate, and dense intensity
   56, 57	Freezing Drizzle: Light and dense intensity
   61, 63, 65	Rain: Slight, moderate and heavy intensity
   66, 67	Freezing Rain: Light and heavy intensity
   71, 73, 75	Snow fall: Slight, moderate, and heavy intensity
   77	Snow grains
   80, 81, 82	Rain showers: Slight, moderate, and violent
   85, 86	Snow showers slight and heavy
   95 *	Thunderstorm: Slight or moderate
   96, 99 *	Thunderstorm with slight and heavy hail
    */
class WeatherConverter {

    private fun convertIntToWeatherCode(weatherCode: Int): WeatherState {
        return when (weatherCode) {
            0 -> WeatherState.ClearSky
            1 -> WeatherState.MainlyClear
            2 -> WeatherState.PartlyCloudy
            3 -> WeatherState.Overcast
            45, 48 -> WeatherState.Fog
            51,53,55, 56,57,61,66,80 -> WeatherState.RainSlight
            63,81 -> WeatherState.RainModerate
            65,67,82 -> WeatherState.RainHeavy
            71,73,75,77,85,86 -> WeatherState.Snow
            95,96,99 -> WeatherState.Thunderstorm
            else -> WeatherState.Other
        }
    }

    /**
     * converts WeatherData-object parsed from API to a display-friendly version of weatherdata: "DailyWeatherDisplay"
     */
    fun getDailyWeatherDisplay(weatherData: WeatherData): List<DailyWeatherDisplay>{
        val displayList = mutableListOf<DailyWeatherDisplay>()
        for (index in weatherData.daily.time.indices) {
            val weatherCode = weatherData.daily.weather_code[index] // Access the corresponding weather code
            val time = weatherData.daily.time[index]
            val temperature = weatherData.daily.temperature_2m_max[index]
            val weather = convertIntToWeatherCode(weatherCode)

            val display = DailyWeatherDisplay(
                time = time,
                weather_State_code = weather,
                temperature_2m_max = temperature
            )
            displayList.add(display)
        }
        return displayList;
    }

    fun getDailyUnits(weatherData: WeatherData): DailyUnits{
        val time: String = weatherData.daily_units.time
        val weather_code: String = weatherData.daily_units.weather_code
        val temperature_2m_max: String = weatherData.daily_units.temperature_2m_max
        val dailyUnits = DailyUnits(time, weather_code,temperature_2m_max)
        return dailyUnits;
    }


    /**
     * converts WeatherData-object parsed from API to a display-friendly version of weatherdata: "DailyWeatherDisplay"
     */
    fun getHourlyWeatherDisplay(weatherData: HourlyWeatherData): List<HourlyDataDisplay> {
        val displayList = mutableListOf<HourlyDataDisplay>()
        for (index in weatherData.hourly.time.indices) {
            val weatherCode = weatherData.hourly.weather_code[index]
            val time = weatherData.hourly.time[index]
            val temperature = weatherData.hourly.temperature_2m[index]
            val relativeHumidity = weatherData.hourly.relative_humidity_2m[index]
            val precipitationProbability = weatherData.hourly.precipitation_probability[index]
            val windSpeed = weatherData.hourly.wind_speed_10m[index]
            val windDirection = weatherData.hourly.wind_direction_10m[index]
            val weatherState = convertIntToWeatherCode(weatherCode)

            val display = HourlyDataDisplay(
                time = time,
                temperature_2m = temperature,
                relative_humidity_2m = relativeHumidity,
                precipitation_probability = precipitationProbability,
                weather_state = weatherState,
                wind_speed_10m = windSpeed,
                wind_direction_10m = windDirection
            )
            displayList.add(display)
        }
        return displayList
    }




    fun getHourlyUnits(weatherData: HourlyWeatherData): HourlyUnits{
        val time = weatherData.hourly_units.time
        val weather_code = weatherData.hourly_units.weather_code
        val temperature_2m = weatherData.hourly_units.temperature_2m
        val relative_humidity_2m =weatherData.hourly_units.relative_humidity_2m
        val wind_direction_10m = weatherData.hourly_units.wind_direction_10m
        val wind_speed_10m = weatherData.hourly_units.wind_speed_10m
        val precipitation_probability = weatherData.hourly_units.precipitation_probability
        val dailyUnits = HourlyUnits(time,temperature_2m, relative_humidity_2m,precipitation_probability,
            weather_code, wind_speed_10m,wind_direction_10m)
        return dailyUnits;
    }
}