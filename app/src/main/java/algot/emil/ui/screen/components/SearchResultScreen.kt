package algot.emil.ui.screen.components

import algot.emil.data.PlaceData
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    places: List<PlaceData>,
    updateWeatherFromQuery: (PlaceData) -> Unit
){

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
                    onClick = {

                        updateWeatherFromQuery(value)}
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