package br.com.lucolimac.shesafe.android.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import br.com.lucolimac.shesafe.android.presentation.state.SecureContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
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
//    private val _secureContact = MutableStateFlow<SecureContact?>(null)
//    val secureContact = _secureContact.asStateFlow()

//    private val _isLoading = MutableStateFlow(true) // Add a loading state
//    val isLoading: StateFlow<Boolean> = _isLoading


    //Genarate a list of 10 contacts with random names and phoneNumber numbers
//    private val _secureContacts = MutableStateFlow<List<SecureContact>>(emptyList())
//    val secureContacts = _secureContacts.asStateFlow()
    fun saveSecureContact(phoneNumber: String, secureContact: SecureContact) {
        viewModelScope.launch {
            if (phoneNumber.isEmpty()) {
                secureContactUseCase.registerSecureContact(
                    secureContact
                ).onStart {
                    startLoad()
                }.onCompletion { finishLoad() }.collect {
                    _uiState.update { uiState ->
                        uiState.copy(
                            hasBeenRegisteredSecureContact = it,
                        )
                    }
                }
            } else {
                secureContactUseCase.updateSecureContact(phoneNumber, secureContact)
                    .onStart { startLoad() }.onCompletion { finishLoad() }.collect {
                        _uiState.update { uiState ->
                            uiState.copy(
                                hasBeenRegisteredSecureContact = it,
                            )
                        }
                    }
            }
        }
        getAllSecureContacts()
    }

    fun getAllSecureContacts() {
        viewModelScope.launch {
            secureContactUseCase.getSecureContacts(
            ).onStart { startLoad() }.onCompletion { finishLoad() }.collect { secureContacts ->
                _uiState.update { currentState ->
                    currentState.copy(
                        secureContacts = secureContacts
                    )
                }
                Log.d("SecureContactViewModel", "Secure contacts: ${uiState.value}")
            }
        }
    }

    fun getSecureContactByPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            secureContactUseCase.getSecureContactByPhoneNumber(phoneNumber).onStart { startLoad() }
                .onCompletion { finishLoad() }.collect { secureContact ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedContact = secureContact,
                        )
                    }
                }
        }
    }

    fun deleteSecureContact(phoneNumber: String) {
        viewModelScope.launch {
            secureContactUseCase.deleteSecureContact(phoneNumber).onStart { startLoad() }
                .onCompletion { finishLoad() }.collect { isDeleted ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            hasBeenDeletedSecureContact = isDeleted,
                        )
                    }
                }
        }
        getAllSecureContacts()
    }

    fun resetRegistered() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(hasBeenRegisteredSecureContact = false)
            }
        }
    }

    fun resetDeleted() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(hasBeenDeletedSecureContact = false)
            }
        }
    }

    fun resetSelectedSecureContact() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(selectedContact = null)
            }
        }
    }

    fun resetLoading() {
        viewModelScope.launch {
            finishLoad()
        }
    }

    fun resetUiState() {
        viewModelScope.launch {
            _uiState.update {
                SecureContactUiState.Empty
            }
        }
    }

    fun startLoad() {
        _uiState.update { currentState ->
            currentState.copy(isLoading = true)
        }
    }

    fun finishLoad() {
        _uiState.update { currentState ->
            currentState.copy(isLoading = false)
        }
    }
}