package algot.emil.ui.screen.components

import algot.emil.R
import algot.emil.data.TopBarProperties
import algot.emil.ui.screen.SearchBar
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlin.reflect.KFunction0

private const val TAG = "TopBar"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    topBarState: TopBarProperties,
    onSearch: (String) -> Unit,
    showSearch: () -> Unit,
    isConnected: () -> Boolean,
    showSnackBar: (String, SnackbarDuration) -> Unit
    ) {

    val isSearchShown = topBarState.isSearchShown
    val searchText = topBarState.searchText


    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = { if (topBarState.isSearchShown) {SearchBar(onSearch = onSearch, searchText = topBarState.searchText) } else { Text(
            text = "PLACE"
        ) } },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors (
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor =  MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = { Icon(painter = painterResource(id = R.drawable.sunny), contentDescription = null) },
        actions = {
            IconButton(onClick = {
                if (isConnected()) {
                    showSearch()
                }
                else {
                    showSnackBar("No connection", SnackbarDuration.Short)
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.search_icon), contentDescription = null)

            }
        }
    )

}