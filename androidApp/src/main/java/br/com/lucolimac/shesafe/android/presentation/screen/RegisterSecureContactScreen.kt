package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.presentation.utils.StringExtensions.validateName
import br.com.lucolimac.shesafe.android.presentation.utils.StringExtensions.validatePhone
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterSecureContactScreen(
    secureContactPhoneNumber: String = "",
    secureContactViewModel: SecureContactViewModel,
    onBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        if (secureContactPhoneNumber.isNotBlank()) {
            secureContactViewModel.getSecureContactByPhoneNumber(secureContactPhoneNumber)
        }
    }

    val secureContact = secureContactViewModel.uiState.collectAsState().value.selectedContact
    var name by remember { mutableStateOf(secureContact?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(secureContact?.phoneNumber ?: "") }
    var nameErrorId by remember { mutableStateOf<Int?>(null) }
    var phoneErrorId by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(secureContact) {
        name = secureContact?.name ?: ""
        phoneNumber = secureContact?.phoneNumber ?: ""
    }
    val isFormValid =
        nameErrorId == null && phoneErrorId == null && name.isNotBlank() && phoneNumber.isNotBlank()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameErrorId = it.validateName()
            },
            label = { Text(stringResource(R.string.label_secure_contact_name)) },
            isError = nameErrorId != null,
            textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth(),
        )
        if (nameErrorId != null) {
            Text(
                stringResource(nameErrorId!!),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneErrorId = it.validatePhone()
            },
            label = { Text(stringResource(R.string.label_secure_contact_phone)) },
            isError = phoneErrorId != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth(),
        )
        if (phoneErrorId != null) {
            Text(
                stringResource(phoneErrorId!!),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                nameErrorId = name.validateName()
                phoneErrorId = phoneNumber.validatePhone()
                if (isFormValid) {
                    secureContactViewModel.saveSecureContact(
                        secureContactPhoneNumber, SecureContact(name, phoneNumber)
                    )
                    onBack()
                }
            },
            shape = RoundedCornerShape(8.dp),
            enabled = isFormValid,
        ) {
            Text(
                text = stringResource(
                    if (secureContactPhoneNumber.isNotBlank()) R.string.label_secure_contact_update else R.string.label_secure_contact_save,
                ),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
fun AddContactScreenPreview() {
    val secureContactViewModel by inject<SecureContactViewModel>(SecureContactViewModel::class.java)
    RegisterSecureContactScreen("", secureContactViewModel, onBack = {})
}
