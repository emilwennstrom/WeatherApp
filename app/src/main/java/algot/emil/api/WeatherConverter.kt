package algot.emil.api

import algot.emil.enums.Weather

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

    fun convertIntToWeatherCode(weatherCode: Int): Weather {
        return when (weatherCode) {
            0 -> Weather.ClearSky
            1 -> Weather.MainlyClear
            2 -> Weather.PartlyCloudy
            3 -> Weather.Overcast
            45, 48 -> Weather.Fog
            51,53,55, 56,57,61,66,80 -> Weather.RainSlight
            63,81 -> Weather.RainModerate
            65,67,82 -> Weather.RainHeavy
            71,73,75,77,85,86 -> Weather.Snow
            95,96,99 -> Weather.Thunderstorm
            else -> Weather.Other
        }
    }

    fun ConvertWeatherDataToVM(weatherData: WeatherData): List<DailyWeatherDisplay>{
        val displayList = mutableListOf<DailyWeatherDisplay>()
        for (index in weatherData.daily.time.indices) {
            val weatherCode = weatherData.daily.weather_code[index] // Access the corresponding weather code
            val time = weatherData.daily.time[index]
            val temperature = weatherData.daily.temperature_2m_max[index]
            val weather = convertIntToWeatherCode(weatherCode)

            val display = DailyWeatherDisplay(
                time = time,
                weather_code = weather,
                temperature_2m_max = temperature
            )
            displayList.add(display)
        }
        return displayList;
    }
}