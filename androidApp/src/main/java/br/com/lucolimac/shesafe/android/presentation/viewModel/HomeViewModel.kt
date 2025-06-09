package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    // This ViewModel can be used to manage the state of the Home screen.
    // Currently, it does not have any specific properties or methods,
    // but it can be extended in the future as needed.

    private val _isCheckboxChecked: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCheckboxChecked: StateFlow<Boolean> = _isCheckboxChecked.asStateFlow()

    fun setCheckboxChecked(value: Boolean) {
        viewModelScope.launch {
            _isCheckboxChecked.emit(value)
        }
    }
}