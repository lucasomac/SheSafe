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
    private val _registered = MutableStateFlow<Boolean>(false)
    val registered = _registered.asStateFlow()


    private val _isLoading = MutableStateFlow(true) // Add a loading state
    val isLoading: StateFlow<Boolean> = _isLoading


    //Genarate a list of 10 contacts with random names and phone numbers
    private val _secureContacts = MutableStateFlow<List<SecureContact>>(emptyList())
    val secureContacts = _secureContacts.asStateFlow()

    fun registerSecureContact(secureContact: SecureContact) {
        viewModelScope.launch {
            secureContactUseCase.registerSecureContact(secureContact)
                .onStart { _isLoading.emit(true) }.onCompletion { _isLoading.emit(false) }.collect {
                    _registered.emit(it)
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

    fun resetRegistered() {
        viewModelScope.launch {
            _registered.emit(false)
        }
    }

}