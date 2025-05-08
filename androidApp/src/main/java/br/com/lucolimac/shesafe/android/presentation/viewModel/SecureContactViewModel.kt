package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class SecureContactViewModel(val secureContactUseCase: SecureContactUseCase) : ViewModel() {

    val PT_BR = Locale("pt", "BR")
    val registered = MutableStateFlow<Boolean>(false)

    //Genarate a list of 10 contacts with random names and phone numbers
    val secureContacts: List<SecureContact> = List(10) { index ->
        val firstName = "Contact${index + 1}"
        val lastName = "Last$index"
        val phoneNumber = generateRandomPhoneNumber()
        val email = "$firstName.$lastName@email.com"
        SecureContact(firstName, lastName, phoneNumber, email)
    }

    private fun generateRandomPhoneNumber(): String {
        val random = Random.Default
        val ddd = String.format(PT_BR, "%02d", random.nextInt(11, 99))
        val number = String.format(PT_BR, "%08d", random.nextInt(10000000, 99999999))
        return "+55$ddd$number"
    }

    fun registerSecureContact() {
        val secureContact = SecureContact(
            "Karina",
            "Silvestre",
            "+5511748565289",
            "john.archibald.campbell@example-pet-store.com"
        )
        viewModelScope.launch {
            secureContactUseCase.registerSecureContact(secureContact).collect {
                registered.emit(it)
            }
        }
    }
}