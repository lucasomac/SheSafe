package br.com.lucolimac.shesafe.android.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.android.R
import br.com.lucolimac.shesafe.android.presentation.component.HomeHeader
import br.com.lucolimac.shesafe.android.presentation.component.contact.ContactCard
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import kotlinx.coroutines.flow.asStateFlow
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureContactsScreen(
    secureContactViewModel: SecureContactViewModel, modifier: Modifier = Modifier
) {
    val registrou = secureContactViewModel.registered.asStateFlow()
    LaunchedEffect(Unit) {
        registrou.collect {
          Log.d("SecureContactsScreen", "SecureContactsScreen has been registered: $it")
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(16.dp)
    ) {
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
                items(secureContactViewModel.secureContacts.size) { index ->
                    ContactCard(secureContact = secureContactViewModel.secureContacts[index])
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