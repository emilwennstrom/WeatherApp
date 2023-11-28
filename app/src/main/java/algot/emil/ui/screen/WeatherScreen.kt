package algot.emil.ui.screen

import algot.emil.R
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(weatherVM: WeatherVM = viewModel()) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    //Greeting(name = weatherVM.name.collectAsState().value);

    Scaffold {
        if (isLandscape) {
            LandscapeScreen(vm = weatherVM, Modifier.padding(it))
        } else {
            PortraitScreen(vm = weatherVM, Modifier.padding(it))
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortraitScreen(vm: WeatherVM, modifier: Modifier) {
    val isLoading = vm.isLoading.collectAsState()
    val searchQuery = vm.searchQuery.collectAsState()
    val searchResults = listOf("Result 1", "Result 2", "Result 3") // Your dynamic data

    LaunchedEffect(Unit) {
        vm.getWeatherNextSevenDays()
        vm.getWeatherHourly()
    }

    Column(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = "Weather Forecast")

        if (!isLoading.value) {
            Row(modifier = modifier.weight(0.4f).fillMaxSize()) {
                ListHourly(vm=vm)
            }
            Row(modifier = modifier.weight(0.3f).fillMaxSize()) {
                ListSevenDays(vm = vm)
            }
        }
        if (searchResults.isEmpty()) {
            Row (modifier.weight(0.2f)){
                ModalDrawerSheet {
                    LazyColumn {
                        items(searchResults) { value ->
                            Text(text = value)
                        }
                    }
                }
            }
        }
        Row(modifier = modifier.padding(10.dp)) {
            SearchBar(onSearch = { query ->
                vm.searchPlaces(query)
            })
        }


    }
}

@ExperimentalMaterial3Api
@Composable
fun SearchBar(
    modifier: Modifier = Modifier, onSearch: (String) -> Unit
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = textState,
        onValueChange = { value ->
            textState = value
            onSearch(value.text)
        },
        singleLine = true,
        placeholder = { Text("Enter place") },
        label = { Text("Search") },
        modifier = Modifier.fillMaxWidth(),
    )
}


@Composable
private fun LandscapeScreen(vm: WeatherVM, padding: Modifier) {
    //Greeting(name = "horisonetelll ")
}

@Composable
private fun ListHourly(vm: WeatherVM, modifier: Modifier = Modifier) {
    val allWeather by vm.allWeatherHourly.collectAsState(initial = listOf())
    val temperatureUnit by vm.temperatureUnit.collectAsState()

    LazyColumn {
        items(allWeather) { weather ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weather.time + " ", style = MaterialTheme.typography.bodyMedium
                    )
                    //Spacer(modifier = Modifier.width(8.dp))
                    weatherImage(vm = vm, weatherState = weather.weatherState.toString())
                    //Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${weather.temperature}$temperatureUnit",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "humidity: ${weather.relativeHumidity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ListSevenDays(vm: WeatherVM, modifier: Modifier = Modifier) {
    val allWeather by vm.allWeather.collectAsState(initial = listOf())
    val temperatureUnit by vm.temperatureUnit.collectAsState()

    LazyColumn {
        items(allWeather) { weather ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weather.time + " ", style = MaterialTheme.typography.bodyMedium
                    )
                    //Spacer(modifier = Modifier.width(8.dp))
                    weatherImage(vm = vm, weatherState = weather.weatherState.toString())
                    //Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${weather.temperature}$temperatureUnit",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun weatherImage(vm: WeatherVM, weatherState: String) {
    Box(
        modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center
    ) {
        // Background image
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Background"
        )
        // Foreground image
        Image(
            painter = painterResource(getPictureName(weatherState)),
            contentDescription = weatherState
        )
    }
}


@Composable
private fun WeatherForSevenDays(vm: WeatherVM, index: Int) {
    val allWeather by vm.allWeather.collectAsState(initial = listOf())
    val temperatureUnit by vm.temperatureUnit.collectAsState()
    vm.loadDayOfWeek(index)
    val currentDay by vm.dayOfWeek.collectAsState()
    val test by vm.dailyWeather.collectAsState()

    Row {
        Box(
            modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center
        ) {
            // Background image
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "Background"
            )
            // Foreground image
            Image(
                painter = painterResource(R.drawable.sunny), contentDescription = "Sunny Weather"
            )
        }
        Box(
            modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center
        ) {
            Text(text = "${test[index].temperature_2m_max}  $temperatureUnit\n")
            Text(text = test[index].time)
        }

        //Text(text = " $currentDay}")
    }
}


/*
ClearSky, MainlyClear, PartlyCloudy, Overcast, RainSlight, RainModerate, RainHeavy,
   Snow, Thunderstorm, Fog, Other
 */
private fun getPictureName(weatherState: String): Int {
    return when (weatherState) {
        "ClearSky" -> R.drawable.sunny
        "MainlyClear" -> R.drawable.sunny
        "PartlyCloudy" -> R.drawable.partly_cloudy_day
        "Overcast" -> R.drawable.cloud
        "RainSlight" -> R.drawable.rainy_light
        "RainModerate" -> R.drawable.rainy_heavy
        "RainHeavy" -> R.drawable.rainy_heavy
        "Snow" -> R.drawable.snowing_heavy
        "Thunderstorm" -> R.drawable.thunderstorm
        "Fog" -> R.drawable.sunny //TODO: lägg till fog
        "Other" -> R.drawable.sunny //TODO: ???
        else -> R.drawable.sunny //TODO: ändra??
    }
}