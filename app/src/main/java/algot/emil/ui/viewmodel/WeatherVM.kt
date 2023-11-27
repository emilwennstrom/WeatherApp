package algot.emil.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherVM : ViewModel() {

    private val _name = MutableStateFlow("Algot")
    val name: StateFlow<String>
        get() = _name






}