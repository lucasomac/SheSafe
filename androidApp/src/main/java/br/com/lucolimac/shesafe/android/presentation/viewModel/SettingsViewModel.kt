package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.usecase.SettingsUseCase
import br.com.lucolimac.shesafe.enum.SettingsEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            settingsUseCase.getToggleSetting(settingsEnum.name).collect { settingToggle ->
                _mapOfSettings.update {
                    it.toMutableMap().also { mutableMap ->
                        mutableMap[settingsEnum] = settingToggle
                    }
                }
            }
        }
    }

    fun setToggleSetting(setting: SettingsEnum, state: Boolean) {
        viewModelScope.launch {
            settingsUseCase.setToggleSetting(setting.name, state).collect { success ->
                if (success) {
                    getToggleSetting(setting)
                }
            }
        }
    }

    fun resetAllStates() {
        viewModelScope.launch {
            _mapOfSettings.emit(mutableMapOf())
        }
    }
}