package algot.emil.ui.screen

import algot.emil.ui.theme.WeatherAppTheme
import algot.emil.ui.viewmodel.WeatherVM
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun WeatherScreen(weatherVM: WeatherVM = viewModel()){

    Greeting(name = weatherVM.name.collectAsState().value);

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        Greeting("Android")
    }
}