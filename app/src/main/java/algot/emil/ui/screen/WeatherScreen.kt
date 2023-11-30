package algot.emil.ui.screen

import algot.emil.R
import algot.emil.persistence.Weather
import algot.emil.persistence.WeatherHourly
import algot.emil.ui.screen.components.SearchResultScreen
import algot.emil.ui.screen.components.TopBar
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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


@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PortraitScreen(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    vm: WeatherVM,
    modifier: Modifier
) {
    val places by vm.places.collectAsState()
    val topBarState by vm.topBarState.collectAsState()
    val sevenDayWeather by vm.allWeather.collectAsState()
    val hourlyWeather by vm.allWeatherHourly.collectAsState()



    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Row {
            var icon = R.drawable.sunny
            if (sevenDayWeather.isNotEmpty()) {
                icon = getPictureName(sevenDayWeather[0].weatherState.toString())
            }

            TopBar(
                topBarState = topBarState,
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
        Row(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxSize()
        ) {
            ListSevenDays(
                sevenDayWeather = sevenDayWeather,
                vm::convertDateToWeekday,
                vm::updateHourly
            )
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
        Row(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxSize()
        ) {
            ListHourly(hourlyWeather)
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
private fun ListHourly(hourlyWeather: List<WeatherHourly>, modifier: Modifier = Modifier) {
    LazyColumn {
        items(hourlyWeather) { weather ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
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
                    WeatherImage(weatherState = weather.weatherState.toString())
                    Column {
                        Text(
                            text = "${weather.temperature} °C ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row {
                            WindImage(degrees = weather.windDirection)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListSevenDays(
    sevenDayWeather: List<Weather>,
    convertDateToWeekday: (String) -> String,
    updateHourly: (String) -> Unit
) {

    LazyRow {
        items(sevenDayWeather) { weather ->
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(2.dp)
                    .aspectRatio(1f / 1f),
                onClick = {
                    updateHourly(weather.time)
                },
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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

@Composable
private fun WindImage(degrees: Int) {
    Box(
        modifier = Modifier,
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
private fun WeatherImage(weatherState: String) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        // Background image
        /* Image(
             painter = painterResource(R.drawable.ic_launcher_background),
             contentDescription = "Background"
         )*/
        // Foreground image
        Image(
            painter = painterResource(getPictureName(weatherState)),
            contentDescription = weatherState
        )
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
        "Snow" -> R.drawable.weather_snowy
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