package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.usecase.HelpMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val helpMessageUseCase: HelpMessageUseCase) : ViewModel() {
    init {
        getHelpMessages()
    }

    private val _helpMessage: MutableStateFlow<String> = MutableStateFlow("")
    val helpMessage: StateFlow<String> = _helpMessage.asStateFlow()
    fun getHelpMessages() = viewModelScope.launch {
        helpMessageUseCase.getHelpMessages().collect { message ->
            _helpMessage.emit(message)
        }
    }

    fun registerHelpMessage(helpMessage: String) {
        viewModelScope.launch {
            helpMessageUseCase.registerHelpMessage(helpMessage).collect { result ->
                // Handle the result of the registration
                if (result) {
                    getHelpMessages()
                }
            }
        }
    }
}