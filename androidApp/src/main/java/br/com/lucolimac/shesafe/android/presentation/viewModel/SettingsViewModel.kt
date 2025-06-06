package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.usecase.SettingsUseCase
import br.com.lucolimac.shesafe.enum.SettingsEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsUseCase: SettingsUseCase) : ViewModel() {
    private val _mapOfSettings = MutableStateFlow(mutableMapOf<SettingsEnum, Boolean>())
    val mapOfSettings: StateFlow<Map<SettingsEnum, Boolean>> = _mapOfSettings.asStateFlow()

    init {
        // Initialize the settings map with default values
        SettingsEnum.entries.forEach { setting ->
            _mapOfSettings.value[setting] = false // Default value can be adjusted as needed
            getToggleSetting(setting) // Load the current setting value
        }
    }

    fun getToggleSetting(settingsEnum: SettingsEnum) {
        viewModelScope.launch {
            settingsUseCase.getToggleSetting(settingsEnum.name).collect {
                _mapOfSettings.value[settingsEnum] = it
            }
        }
    }

    fun setToggleSetting(settingsEnum: SettingsEnum) {
        viewModelScope.launch {
            val currentValue = _mapOfSettings.value[settingsEnum] == true
            val newValue = !currentValue
            settingsUseCase.setToggleSetting(settingsEnum.name, newValue).collect { success ->
                if (success) {
                    _mapOfSettings.emit(_mapOfSettings.value.toMutableMap().apply {
                        this[settingsEnum] = newValue
                    })
                }
            }
        }
    }

    fun resetAllStates() {
        viewModelScope.launch {
            _mapOfSettings.emit(mutableMapOf<SettingsEnum, Boolean>().apply {
                SettingsEnum.entries.forEach { setting ->
                    this[setting] = false // Reset to default value
                }
            })
        }
    }
}