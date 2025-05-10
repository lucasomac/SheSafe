package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class SecureContactViewModel(val secureContactUseCase: SecureContactUseCase) : ViewModel() {

    private val PT_BR = Locale("pt", "BR")
    private val _registered = MutableStateFlow<Boolean>(false)
    val registered = _registered.asStateFlow()

    //Genarate a list of 10 contacts with random names and phone numbers
    private val _secureContacts = MutableStateFlow<List<SecureContact>>(emptyList())
    val secureContacts = _secureContacts.asStateFlow()

    private fun generateRandomPhoneNumber(): String {
        val random = Random.Default
        val ddd = String.format(PT_BR, "%02d", random.nextInt(11, 99))
        val number = String.format(PT_BR, "%08d", random.nextInt(10000000, 99999999))
        return "+55$ddd$number"
    }

    fun registerSecureContact(secureContact: SecureContact) {
        viewModelScope.launch {
            secureContactUseCase.registerSecureContact(secureContact).collect {
                _registered.emit(it)
            }
        }
    }

    fun resetRegistered() {
        viewModelScope.launch {
            _registered.emit(false)
        }
    }

}