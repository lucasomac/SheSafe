package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import br.com.lucolimac.shesafe.android.domain.entity.Contact
import java.util.Locale
import kotlin.random.Random

class ContactsViewModel : ViewModel() {

    val PT_BR = Locale("pt", "BR")

    //Genarate a list of 10 contacts with random names and phone numbers
    val contacts: List<Contact> = List(10) { index ->
        val firstName = "Contact${index + 1}"
        val lastName = "Last$index"
        val phoneNumber = generateRandomPhoneNumber()
        val email = "$firstName.$lastName@email.com"
        Contact(firstName, lastName, phoneNumber, email)
    }

    private fun generateRandomPhoneNumber(): String {
        val random = Random.Default
        val ddd = String.format(PT_BR, "%02d", random.nextInt(11, 99))
        val number = String.format(PT_BR, "%08d", random.nextInt(10000000, 99999999))
        return "+55$ddd$number"
    }
}