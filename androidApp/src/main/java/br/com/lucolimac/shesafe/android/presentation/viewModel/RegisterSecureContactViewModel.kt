package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import br.com.lucolimac.shesafe.android.presentation.state.RegisterSecureContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterSecureContactViewModel(private val secureContactUseCase: SecureContactUseCase) :
    ViewModel() {

    private val _secureContactUiState =
        MutableStateFlow<RegisterSecureContactUiState>(RegisterSecureContactUiState.Empty)
    val secureContactUiState = _secureContactUiState.asStateFlow()

    fun getSecureContactByPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            secureContactUseCase.getSecureContactByPhoneNumber(phoneNumber)
                .onStart { _secureContactUiState.update { RegisterSecureContactUiState.Loading } }
                .collect { secureContact ->
                    secureContact?.let {
                        _secureContactUiState.update {
                            RegisterSecureContactUiState.Success(
                                secureContact = secureContact
                            )
                        }
                    } ?: run {
                        _secureContactUiState.update {
                            RegisterSecureContactUiState.Error("Erro ao obter dados do contato!")
                        }
                    }
                }
        }
    }

    fun saveSecureContact(phoneNumber: String, secureContact: SecureContact) {
        viewModelScope.launch {
            if (phoneNumber.isEmpty()) {
                secureContactUseCase.registerSecureContact(
                    secureContact
                ).onStart {
                    _secureContactUiState.update { RegisterSecureContactUiState.Loading }
                }.collect {
                    if (!it) {
                        _secureContactUiState.update {
                            RegisterSecureContactUiState.Error("Erro ao salvar contato!")
                        }
                        return@collect
                    }
                    _secureContactUiState.update { uiState ->
                        RegisterSecureContactUiState.Success(
                            secureContact = secureContact
                        )
                    }
                }
            } else {
                secureContactUseCase.updateSecureContact(phoneNumber, secureContact)
                    .onStart { _secureContactUiState.update { RegisterSecureContactUiState.Loading } }
                    .collect {
                        if (!it) {
                            _secureContactUiState.update {
                                RegisterSecureContactUiState.Error("Erro ao atualizar contato!")
                            }
                            return@collect
                        }
                        _secureContactUiState.update { uiState ->
                            RegisterSecureContactUiState.Success(
                                secureContact = secureContact
                            )
                        }
                    }
            }
        }
    }
}