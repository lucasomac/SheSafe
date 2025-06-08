package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SecureContactViewModel(val secureContactUseCase: SecureContactUseCase) : ViewModel() {
    private val _hasBeenRegisteredSecureContact = MutableStateFlow(false)
    val hasBeenRegisteredSecureContact = _hasBeenRegisteredSecureContact.asStateFlow()
    private val _hasBeenDeletedSecureContact = MutableStateFlow(false)
    val hasBeenDeletedSecureContact = _hasBeenDeletedSecureContact.asStateFlow()
    private val _secureContact = MutableStateFlow<SecureContact?>(null)
    val secureContact = _secureContact.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // Add a loading state
    val isLoading: StateFlow<Boolean> = _isLoading


    //Genarate a list of 10 contacts with random names and phoneNumber numbers
    private val _secureContacts = MutableStateFlow<List<SecureContact>>(emptyList())
    val secureContacts = _secureContacts.asStateFlow()

    fun regiSecureContact(secureContact: SecureContact) {
        viewModelScope.launch {

        }
    }

    fun saveSecureContact(phoneNumber: String, secureContact: SecureContact) {
        viewModelScope.launch {
            if (phoneNumber.isEmpty()) {
                secureContactUseCase.registerSecureContact(secureContact).onStart {
                    _isLoading.emit(true)
                }.onCompletion { _isLoading.emit(false) }.collect {
                    _hasBeenRegisteredSecureContact.emit(it)
                }
            } else {
                secureContactUseCase.updateSecureContact(phoneNumber, secureContact)
                    .onStart { _isLoading.emit(true) }.onCompletion { _isLoading.emit(false) }
                    .collect { _hasBeenRegisteredSecureContact.emit(it) }
            }
        }
    }

    fun getAllSecureContacts() {
        viewModelScope.launch {
            secureContactUseCase.getSecureContacts(
            ).onStart { _isLoading.emit(true) }.onCompletion { _isLoading.emit(false) }
                .collect { secureContacts ->
                    _secureContacts.emit(secureContacts)
                }
        }
    }

    fun getSecureContactByPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            secureContactUseCase.getSecureContactByPhoneNumber(phoneNumber)
                .onStart { _isLoading.emit(true) }.onCompletion { _isLoading.emit(false) }
                .collect { secureContact ->
                    _secureContact.emit(secureContact)
                }
        }
    }

    fun deleteSecureContact(phoneNumber: String) {
        viewModelScope.launch {
            secureContactUseCase.deleteSecureContact(phoneNumber).onStart { _isLoading.emit(true) }
                .onCompletion { _isLoading.emit(false) }
                .collect { _hasBeenDeletedSecureContact.emit(it) }
        }
    }

    fun resetRegistered() {
        viewModelScope.launch {
            _hasBeenRegisteredSecureContact.emit(false)
        }
    }

    fun resetDeleted() {
        viewModelScope.launch {
            _hasBeenDeletedSecureContact.emit(false)
        }
    }

    fun resetSecureContact() {
        viewModelScope.launch {
            _secureContact.emit(null)
        }
    }

    fun resetLoading() {
        viewModelScope.launch {
            _isLoading.emit(false)
        }
    }

    fun resetAllStates() {
        viewModelScope.launch {
            resetRegistered()
            resetDeleted()
            resetSecureContact()
            resetLoading()
        }
    }
}