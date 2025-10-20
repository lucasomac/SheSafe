package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.usecase.SettingsUseCase
import br.com.lucolimac.shesafe.enum.SettingsEnum
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsUseCase: SettingsUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _mapOfSettings = MutableStateFlow(mutableMapOf<SettingsEnum, Boolean>())
    val mapOfSettings: StateFlow<Map<SettingsEnum, Boolean>> = _mapOfSettings.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            // novo usuário logado -> carregar todas as configurações
            SettingsEnum.entries.forEach { setting ->
                getToggleSetting(setting)
            }
        } else {
            // sem usuário -> limpar o estado
            resetAllStates()
        }
    }

    init {
        // registrar listener para reagir à mudanças de autenticação
        firebaseAuth.addAuthStateListener(authListener)
        // carregar inicialmente se já houver usuário logado
        if (firebaseAuth.currentUser != null) {
            SettingsEnum.entries.forEach { setting ->
                getToggleSetting(setting)
            }
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

    override fun onCleared() {
        firebaseAuth.removeAuthStateListener(authListener)
        super.onCleared()
    }
}