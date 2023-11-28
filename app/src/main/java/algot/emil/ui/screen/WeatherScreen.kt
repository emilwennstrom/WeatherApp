package algot.emil.ui.screen

import algot.emil.R
import algot.emil.ui.theme.WeatherAppTheme
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
                Row {
                    WeatherForSevenDays(vm = vm, 1)
                }
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
        val allWeather by vm.allWeather.collectAsState(initial = listOf())
        Text(text = allWeather.toString())
    }
}


@Composable
private fun LandscapeScreen(vm: WeatherVM) {
    Greeting(name = "horisonetelll ")
}


@Composable
private fun WeatherForSevenDays(vm: WeatherVM, index: Int) {
    val allWeather by vm.allWeather.collectAsState(initial = listOf())
    val temperatureUnit by vm.temperatureUnit.collectAsState()
    vm.loadDayOfWeek(index)
    val currentDay by vm.dayOfWeek.collectAsState()

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
            painter = painterResource(R.drawable.sunny_24px),
            contentDescription = "Sunny Weather"
        )
    }
    Box(
        modifier = Modifier
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = " $temperatureUnit")
    }

    Text(text = " $currentDay}")

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