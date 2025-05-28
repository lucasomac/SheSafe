package br.com.lucolimac.shesafe.android.presentation.screen

import SheSafeBottomSheet
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.presentation.component.HomeHeader
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeLoading
import br.com.lucolimac.shesafe.android.presentation.component.contact.SecureContactCard
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureContactsScreen(
    onEditAction: (SecureContact) -> Unit,
    onDeleteAction: (SecureContact) -> Unit,
    secureContactViewModel: SecureContactViewModel,
    modifier: Modifier = Modifier
) {
    val secureContacts by secureContactViewModel.secureContacts.collectAsState()
    val isLoading by secureContactViewModel.isLoading.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<SecureContact?>(null) }
    LaunchedEffect(Unit) {
        secureContactViewModel.getAllSecureContacts()
    }
    Column {
        HomeHeader(
            stringResource(R.string.title_secure_contacts),
            modifier = modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        when {
            isLoading -> {
                SheSafeLoading()
            }

            secureContacts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.message_no_secure_contacts),
                        modifier = modifier.align(Alignment.Center)
                    )
                }
            }

            showBottomSheet -> ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                }, sheetState = sheetState
            ) {
                SheSafeBottomSheet(
                    contactName = selectedContact?.name ?: "", onConfirmDelete = {
                    onDeleteAction(selectedContact ?: return@SheSafeBottomSheet)
                    selectedContact = null
                    secureContactViewModel.getAllSecureContacts()
                    showBottomSheet = false
                }, onDismiss = {
                    showBottomSheet = false
                }, sheetState = sheetState
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 72.dp)
                ) {
                    items(secureContacts.size) { index ->
                        SecureContactCard(
                            secureContact = secureContacts[index],
                            onEditClick = onEditAction,
                            onDeleteClick = {
                                selectedContact = secureContacts[index]
                                showBottomSheet = true
                            },
                        )
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
    SecureContactsScreen({}, {}, viewModel, modifier = Modifier)
}