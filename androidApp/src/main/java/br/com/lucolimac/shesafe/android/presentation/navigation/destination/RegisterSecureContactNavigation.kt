package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.RegisterSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.RegisterSecureContactViewModel

const val BASE_SECURE_CONTACT_ROUTE = "registercontact"
const val SECURE_CONTACT_PHONE_NUMBER_ARGUMENT = "secureContactPhoneNumber"
const val REGISTER_SECURE_CONTACT_ROUTE =
    "${BASE_SECURE_CONTACT_ROUTE}/{$SECURE_CONTACT_PHONE_NUMBER_ARGUMENT}"

fun NavGraphBuilder.registerSecureContactScreen(
    registerSecureContactViewModel: RegisterSecureContactViewModel, navController: NavHostController
) {
    composable(REGISTER_SECURE_CONTACT_ROUTE) {
        val secureContactPhoneNumber =
            it.arguments?.getString(SECURE_CONTACT_PHONE_NUMBER_ARGUMENT) ?: ""
        LaunchedEffect(Unit) {
            // Resetar o estado do ViewModel para limpar dados anteriores
            registerSecureContactViewModel.resetState()
            
            // Se há um número de telefone, carregar os dados do contato para edição
            secureContactPhoneNumber.takeIf { phoneNumber -> phoneNumber.isNotEmpty() }
                ?.let { phoneNumber ->
                    registerSecureContactViewModel.getSecureContactByPhoneNumber(phoneNumber)
                }
        }
        val state by registerSecureContactViewModel.secureContactUiState.collectAsState()
        RegisterSecureContactScreen(
            secureContactPhoneNumber = secureContactPhoneNumber,
            state = state,
            onBack = {
                navController.navigateUp()
            },
            onSaveContact = { phoneNumber, secureContact ->
                registerSecureContactViewModel.saveSecureContact(phoneNumber, secureContact)
            })
    }
}

fun NavHostController.navigateToRegisterSecureContact(secureContactPhoneNumber: String = "") {
    this.navigate("${BASE_SECURE_CONTACT_ROUTE}/$secureContactPhoneNumber") {
        // Não restaurar estado para garantir formulário limpo
        launchSingleTop = true
        restoreState = false
    }
}