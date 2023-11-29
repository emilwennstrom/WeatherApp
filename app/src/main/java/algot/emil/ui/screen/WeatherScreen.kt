package algot.emil.ui.screen

import algot.emil.R
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import android.os.Debug
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
    val isLoading by vm.isLoading.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val places by vm.places.collectAsState()
    val isSearching by vm.isSearching.collectAsState()
    
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Row {
            CenterAlignedTopAppBar(
                modifier = modifier.fillMaxWidth(),
                title = { SearchBar(onSearch = vm::onSearchTextChanged, searchText = searchQuery)},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors (
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor =  MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = { Icon(painter = painterResource(id = R.drawable.sunny), contentDescription = null)},
                actions = {
                    IconButton(onClick = { vm.getWeatherFromDb() }) {
                        Icon(painter = painterResource(id = R.drawable.settings), contentDescription = null)
                        
                    }
                }
            )
        }

        if (searchQuery.isNotEmpty()) {
            Row(modifier.weight(1.2f)) {
                ModalDrawerSheet(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        contentPadding = PaddingValues(10.dp),
                        modifier = modifier.fillMaxHeight()

                    ) {
                        items(places) { value ->
                            Card(
                                modifier = modifier
                                    .fillMaxSize()
                                    .padding(2.dp)
                                    .height(50.dp),
                                onClick = {vm.updateWeatherFromQuery(value)}
                            ) {
                                Row(
                                    modifier = modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = value.display_name , style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
        if (!isLoading) {

            Row(modifier = Modifier
                .weight(0.2f)
                .fillMaxSize()) {
                ListSevenDays(vm = vm)
            }
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            Row(modifier = Modifier
                .weight(0.8f)
                .fillMaxSize()) {
                ListHourly(vm=vm)
            }
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@ExperimentalMaterial3Api
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
    searchText: String
) {
    TextField(
        value = searchText,
        onValueChange = onSearch, //onValueChange = onSearch,
        keyboardActions = KeyboardActions(
            onDone = {onSearch(searchText)}
        ),
        singleLine = true,
        placeholder = { Text("Enter place") },
        label = { Text("Search") },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RectangleShape
    )
}


@Composable
private fun LandscapeScreen(vm: WeatherVM, padding: Modifier) {
    //Greeting(name = "horisonetelll ")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ListHourly(vm: WeatherVM, modifier: Modifier = Modifier) {
    val allWeather by vm.allWeatherHourly.collectAsState()
    val temperatureUnit by vm.temperatureUnit.collectAsState()

    LazyColumn (){
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
                        text = weather.time.takeLast(5) + " ", style = MaterialTheme.typography.bodyMedium
                    )
                    //Spacer(modifier = Modifier.width(8.dp))
                    weatherImage(vm = vm, weatherState = weather.weatherState.toString())
                    //Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${weather.temperature} °C ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "rel. humidity: ${weather.relativeHumidity} % ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "precipitation prob.: ${weather.precipitationProbability} %",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "wind speed: ${weather.windSpeed} km/h",
                        style = MaterialTheme.typography.bodyMedium
                    )
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
                        text = vm.convertDateToWeekday(weather.time ) + " ", style = MaterialTheme.typography.bodyMedium
                    )
                    weatherImage(vm = vm, weatherState = weather.weatherState.toString())
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