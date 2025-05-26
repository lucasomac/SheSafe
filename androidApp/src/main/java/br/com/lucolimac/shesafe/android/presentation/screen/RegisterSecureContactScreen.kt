package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
    secureContactPhone: String = "",
    secureContactViewModel: SecureContactViewModel,
    onBack: () -> Unit,
) {
    var isEditMode by remember { mutableStateOf(secureContactPhone.isNotBlank()) }
    val isLoading = secureContactViewModel.isLoading.collectAsState().value
    LaunchedEffect(Unit) {
        if (isEditMode) {
            secureContactViewModel.getSecureContactByPhone(secureContactPhone)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                // Show a loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                var name: String by remember {
                    mutableStateOf(
                        secureContactViewModel.secureContact.value?.name ?: "",
                    )
                }
                var phoneNumber: String by remember {
                    mutableStateOf(
                        secureContactViewModel.secureContact.value?.phoneNumber ?: "",
                    )
                }
                val registered by secureContactViewModel.hasBeenRegisteredSecureContact.collectAsState()
                if (registered) {
                    // Handle the case when the contact is registered
                    // For example, you can show a success message or navigate back
                    onBack()
                }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text =
                                        stringResource(
                                            if (isEditMode) R.string.title_edit_secure_contact else R.string.title_new_secure_contact,
                                        ),
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                    )
                                }
                            },
                            colors =
                                TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                ),
                        )
                    },
                ) { paddingValues ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top, // Changed to Top
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome") },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { newPhoneNumber ->
                                if (newPhoneNumber.all { it.isDigit() }) {
                                    phoneNumber = newPhoneNumber
                                }
                            },
                            label = { Text("Celular") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.weight(1f)) // Push button to the bottom

                        Button(
                            onClick = {
                                if (isEditMode) {
                                    secureContactViewModel.updateSecureContact(
                                        secureContactViewModel.secureContact.value?.phoneNumber!!,
                                        SecureContact(
                                            name,
                                            phoneNumber,
                                        ),
                                    )
                                } else {
                                    secureContactViewModel.registerSecureContact(
                                        SecureContact(
                                            name,
                                            phoneNumber,
                                        ),
                                    )
                                }
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = "SALVAR",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }
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
