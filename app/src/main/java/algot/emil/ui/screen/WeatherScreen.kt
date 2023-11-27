package algot.emil.ui.screen

import algot.emil.ui.theme.WeatherAppTheme
import algot.emil.ui.viewmodel.WeatherVM
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun WeatherScreen(weatherVM: WeatherVM = viewModel()){

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    //Greeting(name = weatherVM.name.collectAsState().value);
    if(isLandscape){
        LandscapeScreen(vm = weatherVM)
    }else{
        PortraitScreen(vm = weatherVM)
    }

}


@Composable
private fun PortraitScreen(vm: WeatherVM){Greeting(name = "tja");
    Column{
        Button(
            modifier = Modifier
                .padding(horizontal = 42.dp)
                .weight(0.1f),
            onClick = {
                vm.getWeatherNextSevenDays()
            }) {
            Text(
                text = "Get Weather"
            )

        }
    }


}

@Composable
private fun LandscapeScreen(vm: WeatherVM){
    Greeting(name = "horisonetelll ");
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