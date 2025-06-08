package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.RegisterSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel

const val BASE_SECURE_CONTACT_ROUTE = "registercontact"
 const val SECURE_CONTACT_PHONE_NUMBER_ARGUMENT = "secureContactPhoneNumber"
const val REGISTER_CONTACT_ROUTE =
    "${BASE_SECURE_CONTACT_ROUTE}/{$SECURE_CONTACT_PHONE_NUMBER_ARGUMENT}"

fun NavGraphBuilder.registerSecureContactScreen(
    secureContactViewModel: SecureContactViewModel, navController: NavHostController
) {
    composable(REGISTER_CONTACT_ROUTE) {
        val secureContactPhoneNumber =
            it.arguments?.getString(SECURE_CONTACT_PHONE_NUMBER_ARGUMENT) ?: ""
        RegisterSecureContactScreen(
            secureContactPhoneNumber = secureContactPhoneNumber, secureContactViewModel,
            onBack = {
                navController.navigateUp()
                secureContactViewModel.resetSecureContact()
            },
        )
    }
}

fun NavHostController.navigateToRegisterSecureContact(secureContactPhoneNumber: String = "") {
    this.navigate("${BASE_SECURE_CONTACT_ROUTE}/$secureContactPhoneNumber") {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}