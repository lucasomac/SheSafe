package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.android.R
import br.com.lucolimac.shesafe.android.presentation.component.HomeHeader
import br.com.lucolimac.shesafe.android.presentation.component.contact.ContactCard
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureContactsScreen(
    secureContactViewModel: SecureContactViewModel, modifier: Modifier = Modifier
) {
    // Observe the secure contacts from the ViewModel
    val secureContacts = secureContactViewModel.secureContacts.collectAsState().value
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(16.dp)
    ) {
        if (secureContacts.isEmpty()) {
            // Show a message or UI indicating that there are no secure contacts
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeHeader(
                    stringResource(R.string.title_no_secure_contacts),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                // You can add a button to navigate to the registration screen
                // or any other UI element to guide the user.
            }
        } else {
            // Show the list of secure contacts
            Column {
                HomeHeader(
                    stringResource(R.string.title_contacts),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 72.dp) // Space for FAB

                ) {
                    items(secureContacts.size) { index ->
                        ContactCard(secureContact = secureContacts[index])
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreview() {
    val viewModel: SecureContactViewModel by inject(SecureContactViewModel::class.java)
    SecureContactsScreen(viewModel, modifier = Modifier)
}