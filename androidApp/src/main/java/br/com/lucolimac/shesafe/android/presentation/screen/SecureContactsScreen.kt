package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    val isLoading = secureContactViewModel.isLoading.collectAsState().value
    LaunchedEffect(Unit) {
        // Fetch the secure contacts when the screen is launched
        secureContactViewModel.getAllSecureContacts()
    }
    // Show a loading indicator while fetching data
    Box(modifier = modifier.fillMaxSize()) { // Use a Box to layer content
        when {
            isLoading -> {
                // Show a loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            secureContacts.isEmpty() -> {
                // Show a message or UI indicating that there are no secure contacts
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center), // Center the column
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HomeHeader(
                        stringResource(R.string.title_no_secure_contacts),
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            else -> {
                // Show the list of secure contacts
                Column {
                    HomeHeader(
                        stringResource(R.string.title_contacts),
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreview() {
    val viewModel: SecureContactViewModel by inject(SecureContactViewModel::class.java)
    SecureContactsScreen(viewModel, modifier = Modifier)
}