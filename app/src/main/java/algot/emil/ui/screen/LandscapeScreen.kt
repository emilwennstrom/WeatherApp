package algot.emil.ui.screen

import algot.emil.R
import algot.emil.ui.screen.components.SearchResultScreen
import algot.emil.ui.screen.components.TopBar
import algot.emil.ui.viewmodel.WeatherVM
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LandscapeScreen(
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
                .weight(0.3f)
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
                .weight(0.7f)
                .fillMaxSize()
        ) {
            ListHourly(hourlyWeather)
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}
