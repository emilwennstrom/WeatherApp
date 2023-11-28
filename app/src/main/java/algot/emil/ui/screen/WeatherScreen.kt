package algot.emil.ui.screen

import algot.emil.R
import algot.emil.ui.theme.WeatherAppTheme
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun WeatherScreen(weatherVM: WeatherVM = viewModel()) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    //Greeting(name = weatherVM.name.collectAsState().value);
    if (isLandscape) {
        LandscapeScreen(vm = weatherVM)
    } else {
        PortraitScreen(vm = weatherVM)
    }

}


@Composable
private fun PortraitScreen(vm: WeatherVM) {
    Greeting(name = "tja")
    val isLoading = vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.getWeatherNextSevenDays()
    }

    Column {
        Column(
            modifier = Modifier
                .padding(42.dp)
                .weight(0.8f)
        ) {
            if (!isLoading.value) {
               ListTest(vm = vm)
            }
        }
        Column(
            modifier = Modifier
                .padding(42.dp)
                .weight(0.2f),
        ) {
            Button(
                onClick = {
                    vm.getWeatherNextSevenDays()
                }) {
                Text(
                    text = "Get Weather"
                )

            }
        }
        //val allWeather by vm.allWeather.collectAsState(initial = listOf())
        //Text(text = allWeather.toString())

    }
}



@Composable
private fun LandscapeScreen(vm: WeatherVM) {
    Greeting(name = "horisonetelll ")
}

@Composable
private fun ListTest(vm: WeatherVM) {
    val allWeather by vm.allWeather.collectAsState(initial = listOf())
    val temperatureUnit by vm.temperatureUnit.collectAsState()

    LazyColumn {
        items(allWeather) { weather ->
            Row {
                Text(text = weather.time+ " ")
                weatherImage(vm = vm, weatherState =weather.weatherState.toString() )
                Text(text = weather.temperature.toString() + temperatureUnit)
            }
            Spacer(modifier = Modifier
                .width(8.dp)
                .height(8.dp))
        }
    }
}

@Composable
private fun weatherImage(vm: WeatherVM, weatherState: String){
    Box(
        modifier = Modifier
            .height(48.dp),
        contentAlignment = Alignment.Center
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

    Row{
    Box(
        modifier = Modifier
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background image
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Background"
        )
        // Foreground image
        Image(
            painter = painterResource(R.drawable.sunny),
            contentDescription = "Sunny Weather"
        )
    }
    Box(
        modifier = Modifier
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "${test.get(index).temperature_2m_max}  $temperatureUnit\n")
        Text(text = "${test.get(index).time}")
    }

    //Text(text = " $currentDay}")
    }
}

@Composable
private fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    WeatherAppTheme {
        WeatherScreen()
    }
}

/*
ClearSky, MainlyClear, PartlyCloudy, Overcast, RainSlight, RainModerate, RainHeavy,
   Snow, Thunderstorm, Fog, Other
 */
private fun getPictureName(weatherState: String):Int{
    return when (weatherState){
        "ClearSky" -> R.drawable.sunny
        "MainlyClear" -> R.drawable.sunny
        "PartlyCloudy" -> R.drawable.partly_cloudy_day
        "Overcast" -> R.drawable.cloud
        "RainSlight" ->R.drawable.rainy_light
        "RainModerate" -> R.drawable.rainy_heavy
        "RainHeavy" -> R.drawable.rainy_heavy
        "Snow" ->R.drawable.snowing_heavy
        "Thunderstorm" -> R.drawable.thunderstorm
        "Fog" -> R.drawable.sunny //TODO: lägg till fog
        "Other" -> R.drawable.sunny //TODO: ???
        else -> R.drawable.sunny //TODO: ändra??
    }
}