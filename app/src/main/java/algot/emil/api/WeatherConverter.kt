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

    fun convertIntToWeatherCode(weatherCode: Int): WeatherState {
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
}