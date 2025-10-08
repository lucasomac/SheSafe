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
import br.com.lucolimac.shesafe.android.presentation.state.SecureContactUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureContactsScreen(
    uiState: SecureContactUiState,
    onEditAction: (SecureContact) -> Unit,
    onDeleteAction: (SecureContact) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheetDelete by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<SecureContact?>(null) }
    Column {
        HomeHeader(
            stringResource(R.string.title_secure_contacts),
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
        when (uiState) {
            SecureContactUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.message_no_secure_contacts),
                        modifier = modifier.align(Alignment.Center)
                    )
                }
            }

            is SecureContactUiState.Error -> TODO()
            SecureContactUiState.Loading -> SheSafeLoading()
            is SecureContactUiState.Success -> {
                val secureContacts = uiState.secureContacts
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
                                showBottomSheetDelete = true
                            },
                            onSelectedContact = {
                                selectedContact = it
                            })
                    }
                }
            }

            SecureContactUiState.Idle -> return
        }
        when {
            showBottomSheetDelete -> ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheetDelete = false
                }, sheetState = sheetState
            ) {
                SheSafeBottomSheet(
                    contactName = selectedContact?.name ?: "",
                    onConfirmDelete = {
                        onDeleteAction(selectedContact ?: return@SheSafeBottomSheet)
                        selectedContact = null
                        showBottomSheetDelete = false
                    },
                    onDismiss = {
                        showBottomSheetDelete = false
                    },
                    sheetState = sheetState,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreview() {
    SecureContactsScreen(
        uiState = SecureContactUiState.Success(
        listOf(
            SecureContact(
                name = "Contato 1",
                phoneNumber = "123456789",
            ),
            SecureContact(
                name = "Contato 2",
                phoneNumber = "987654321",
            ),
        )
    ), onEditAction = {}, onDeleteAction = {})
}