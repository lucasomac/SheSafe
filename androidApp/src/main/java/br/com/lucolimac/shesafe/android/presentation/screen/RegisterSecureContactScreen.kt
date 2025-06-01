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

    val secureContact by secureContactViewModel.secureContact.collectAsState()
    var name by remember { mutableStateOf(secureContact?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(secureContact?.phoneNumber ?: "") }
    LaunchedEffect(secureContact) {
        name = secureContact?.name ?: ""
        phoneNumber = secureContact?.phoneNumber ?: ""
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.label_secure_contact_name)) },
            textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(stringResource(R.string.label_secure_contact_phone)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.weight(1F))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                secureContactViewModel.saveSecureContact(
                    secureContactPhoneNumber, SecureContact(name, phoneNumber)
                )
                onBack()
            },
            shape = RoundedCornerShape(8.dp),
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
