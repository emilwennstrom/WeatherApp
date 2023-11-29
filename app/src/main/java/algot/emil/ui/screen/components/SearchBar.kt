package algot.emil.ui.screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape

@ExperimentalMaterial3Api
@Composable
fun SearchBar(
    modifier: Modifier = Modifier, onSearch: (String) -> Unit, searchText: String
) {
    TextField(value = searchText,
        onValueChange = onSearch, //onValueChange = onSearch,
        keyboardActions = KeyboardActions(onDone = { onSearch(searchText) }),
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