package algot.emil.ui.screen

import algot.emil.R
import algot.emil.ui.screen.components.SearchResultScreen
import algot.emil.ui.screen.components.TopBar
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(weatherVM: WeatherVM = viewModel()) {


    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }


    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) {
        if (isLandscape) {
            LandscapeScreen(vm = weatherVM, Modifier.padding(it))
        } else {
            PortraitScreen(scope, snackBarHostState, vm = weatherVM, Modifier.padding(it))
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortraitScreen(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    vm: WeatherVM,
    modifier: Modifier
) {
    val isLoading by vm.isLoading.collectAsState()
    val places by vm.places.collectAsState()
    val topBarState by vm.topBarState.collectAsState()
    val currentPlace by vm.currentPlace.collectAsState()
    val sevenDayWeather by vm.allWeather.collectAsState()



    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Row {

            var icon = R.drawable.sunny
            if (sevenDayWeather.isNotEmpty()){
                icon = getPictureName(sevenDayWeather[0].weatherState.toString())
            }

            TopBar(
                topBarState = topBarState,
                currentPlace = currentPlace,
                onSearch = vm::onSearchTextChanged,
                showSearch = vm::showSearch,
                isConnected = vm::getConnectivity,
                showSnackBar = { message, duration ->
                    showSnackbar(
                        scope, snackbarHostState, message, duration
                    )
                },
                resetTextField = vm::updateTopBarTextField,
                weatherIcon = icon
            )

        }

        if (topBarState.searchText.isNotEmpty() && topBarState.isSearchShown) {
            Row(modifier.weight(1.2f)) {
                SearchResultScreen(modifier = Modifier, places, vm::updateWeatherFromQuery)
            }
        }

        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
        if (!isLoading) {

            Row(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize()
            ) {
                ListSevenDays(vm = vm)
            }
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            Row(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxSize()
            ) {
                ListHourly(vm = vm)
            }
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}




@Composable
private fun LandscapeScreen(vm: WeatherVM, modifier: Modifier) {
    //Greeting(name = "horisonetelll ")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ListHourly(vm: WeatherVM, modifier: Modifier = Modifier) {
    val allWeather by vm.allWeatherHourly.collectAsState()
    val temperatureUnit by vm.temperatureUnit.collectAsState()

    LazyColumn {
        items(allWeather) { weather ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
            ) {
                FlowRow(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weather.time.takeLast(5) + " ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    //Spacer(modifier = Modifier.width(8.dp))
                    weatherImage(weatherState = weather.weatherState.toString())
                    //Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "${weather.temperature} °C ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row {
                            windImage(vm = vm, degrees = weather.windDirection)
                            Text(
                                text = "${weather.windSpeed} km/h",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }
                    Column {
                        Text(
                            text = "rel. humidity: ${weather.relativeHumidity} % ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Precip. prob.: ${weather.precipitationProbability} %",
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }


                }
            }
        }
    }
}

@Composable
private fun ListSevenDays(vm: WeatherVM, modifier: Modifier = Modifier) {
    val allWeather by vm.allWeather.collectAsState()
    val temperatureUnit by vm.temperatureUnit.collectAsState()

    LazyRow {
        items(allWeather) { weather ->
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(2.dp)
                    .aspectRatio(1f / 1f),
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = vm.convertDateToWeekday(weather.time) + " ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    weatherImage(weatherState = weather.weatherState.toString())
                    Text(
                        text = "${weather.temperature} °C",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun windImage(vm: WeatherVM, degrees: Int) {
    Box(
        modifier = Modifier,
        //.height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(R.drawable.arrow),
            contentDescription = "degrees: $degrees°",
            modifier = Modifier.graphicsLayer {
                rotationZ = degrees.toFloat() // Rotate in z-direction
            })
    }
}

@Composable
private fun weatherImage(weatherState: String) {
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

private fun showSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
    duration: SnackbarDuration
) {
    scope.launch {
        snackbarHostState.showSnackbar(
            message = message, actionLabel = "Close", duration = duration
        )
    }
}