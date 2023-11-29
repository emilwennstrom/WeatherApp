package algot.emil.ui.screen.components

import algot.emil.R
import algot.emil.data.TopBarProperties
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
import androidx.compose.ui.text.style.TextOverflow

private const val TAG = "TopBar"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    topBarState: TopBarProperties,
    onSearch: (String) -> Unit,
    showSearch: () -> Unit,
    isConnected: () -> Boolean,
    showSnackBar: (String, SnackbarDuration) -> Unit,
    resetTextField: (String) -> Unit,
    weatherIcon: Int
) {

    CenterAlignedTopAppBar(modifier = modifier.fillMaxWidth(),
        title = {
            if (topBarState.isSearchShown && isConnected()) {
                SearchBar(onSearch = onSearch, searchText = topBarState.searchText)
            } else {
                if (!isConnected()) {
                    showSnackBar("No connection", SnackbarDuration.Short)
                }
                Text(
                    text = topBarState.currentPlace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                resetTextField("")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = {
            Icon(
                painter = painterResource(id = weatherIcon),
                contentDescription = null
            )
        },
        actions = {
            IconButton(onClick = {
                if (isConnected()) {
                    showSearch()
                } else {
                    showSnackBar("No connection", SnackbarDuration.Short)
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = null
                )

            }
        })

}