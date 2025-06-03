package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.RegisterSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.route.SheSafeDestination

private const val SECURE_CONTACT_PHONE_NUMBER_ARGUMENT = "secureContactPhoneNumber"

fun NavGraphBuilder.registerSecureContactScreen(
    secureContactViewModel: SecureContactViewModel, navController: NavHostController
) {
    composable("${SheSafeDestination.RegisterContact.route.name}/$SECURE_CONTACT_PHONE_NUMBER_ARGUMENT") {
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
    this.navigate("${SheSafeDestination.RegisterContact.route.name} /$secureContactPhoneNumber") {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}