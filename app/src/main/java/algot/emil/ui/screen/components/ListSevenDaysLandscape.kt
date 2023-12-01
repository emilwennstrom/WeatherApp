package algot.emil.ui.screen.components

import algot.emil.persistence.Weather
import algot.emil.ui.screen.WeatherImage
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSevenDays(
    sevenDayWeather: List<Weather>,
    selectedDate: String,
    convertDateToWeekday: (String) -> String,
    updateHourly: (String) -> Unit
) {
    LazyColumn{
        items(sevenDayWeather) { weather ->
            Log.d("view", weather.time.toString())
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(2.dp)
                    .aspectRatio(1f / 1f),
                onClick = {
                    updateHourly(weather.time)
                },
                colors = if (weather.time == selectedDate) {
                    CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly

                ) {
                    Text(
                        text = convertDateToWeekday(weather.time) + " ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    WeatherImage(weatherState = weather.weatherState.toString())
                    Text(
                        text = "${weather.temperature} °C",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}