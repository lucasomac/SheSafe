package br.com.lucolimac.shesafe.android.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import br.com.lucolimac.shesafe.android.presentation.state.SecureContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SecureContactViewModel(val secureContactUseCase: SecureContactUseCase) : ViewModel() {
    // StateFlow to manage the state of secure contacts
    private val _uiState: MutableStateFlow<SecureContactUiState> =
        MutableStateFlow(SecureContactUiState.Empty)
    val uiState: StateFlow<SecureContactUiState> = _uiState.asStateFlow()
//    private val _hasBeenRegisteredSecureContact = MutableStateFlow(false)
//    val hasBeenRegisteredSecureContact = _hasBeenRegisteredSecureContact.asStateFlow()
//    private val _hasBeenDeletedSecureContact = MutableStateFlow(false)
//    val hasBeenDeletedSecureContact = _hasBeenDeletedSecureContact.asStateFlow()

//    fun setSelectedSecureContact(secureContact: SecureContact) {
//        viewModelScope.launch {
//            _uiState.update { currentState ->
//                currentState.copy(
//                    selectedContact = secureContact,
//                )
//            }
//        }
//    }

    fun getAllSecureContacts() {
        viewModelScope.launch {
            secureContactUseCase.getSecureContacts(
            ).onStart { startLoad() }.collect { secureContacts ->
                if (secureContacts.isEmpty()) {
                    _uiState.update {
                        SecureContactUiState.Empty
                    }
                } else {
                    _uiState.update {
                        SecureContactUiState.Success(secureContacts)
                    }
                }
                Log.d("SecureContactViewModel", "Secure contacts: ${uiState.value}")
            }
        }
    }


    fun deleteSecureContact(phoneNumber: String) {
        viewModelScope.launch {
            secureContactUseCase.deleteSecureContact(phoneNumber).onStart { startLoad() }
                .collect { isDeleted ->
                    if (isDeleted) {
                        getAllSecureContacts()
                    } else {
                        _uiState.update { SecureContactUiState.Error("Não foi possível concluir a colicitação!") }
                    }
                }
            getAllSecureContacts()
        }
    }

//    fun resetRegistered() {
//        viewModelScope.launch {
//            _uiState.update { currentState ->
//                currentState.copy(hasBeenRegisteredSecureContact = false)
//            }
//        }
//    }
//
//    fun resetDeleted() {
//        viewModelScope.launch {
//            _uiState.update { currentState ->
//                currentState.copy(hasBeenDeletedSecureContact = false)
//            }
//        }
//    }
//
//    fun resetSelectedSecureContact() {
//        viewModelScope.launch {
//            _uiState.update { currentState ->
//                currentState.copy(selectedContact = null)
//            }
//        }
//    }

//    fun resetLoading() {
//        viewModelScope.launch {
//            finishLoad()
//        }
//    }

//    fun resetUiState() {
//        viewModelScope.launch {
//            _uiState.update {
//                SecureContactUiState.Empty
//            }
//        }
//    }

    fun startLoad() {
        _uiState.update {
            SecureContactUiState.Loading
        }
    }

//    fun finishLoad() {
//        _uiState.update { currentState ->
//            currentState.copy(isLoading = false)
//        }
//    }
}