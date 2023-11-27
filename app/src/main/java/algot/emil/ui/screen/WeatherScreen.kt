package algot.emil.ui.screen

import algot.emil.ui.theme.WeatherAppTheme
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun WeatherScreen(weatherVM: WeatherVM = viewModel()){

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Greeting(name = weatherVM.name.collectAsState().value);

}


@Composable
private fun HorizontalScreen(vm: WeatherVM){

}


@Composable
private fun VerticalScreen(vm: WeatherVM){

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
        WeatherScreen();
    }
}