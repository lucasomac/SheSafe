package br.com.lucolimac.shesafe.android.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val secureContactUseCase: SecureContactUseCase) : ViewModel() {
    // This ViewModel can be used to manage the state of the Home screen.
    // Currently, it does not have any specific properties or methods,
    // but it can be extended in the future as needed.

    private val _isCheckboxChecked: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCheckboxChecked: StateFlow<Boolean> = _isCheckboxChecked.asStateFlow()
    private val _secureContacts = MutableStateFlow<List<SecureContact>>(emptyList())
    val secureContacts: StateFlow<List<SecureContact>> = _secureContacts.asStateFlow()
    fun setCheckboxChecked(value: Boolean) {
        viewModelScope.launch {
            _isCheckboxChecked.emit(value)
        }
    }

    fun getAllSecureContacts() {
        viewModelScope.launch {
            secureContactUseCase.getSecureContacts(
            ).collect { secureContacts ->
                _secureContacts.update { secureContacts }
                Log.d("SecureContactViewModel", "Secure contacts: ${_secureContacts.value}")
            }
        }
    }
}