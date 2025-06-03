package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.SecureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.route.SheSafeDestination

fun NavGraphBuilder.secureContactsScreen(
    navController: NavHostController, secureContactViewModel: SecureContactViewModel
) {
    composable(SheSafeDestination.Contacts.route.name) {
        SecureContactsScreen(
            onEditAction = { secureContact ->
                navController.navigateToRegisterSecureContact(secureContact.phoneNumber)
            },
            onDeleteAction = {
                secureContactViewModel.deleteSecureContact(it.phoneNumber)
            },
            secureContactViewModel,
        )
    }
}

fun NavHostController.navigateToSecureContacts() {
    this.navigate(SheSafeDestination.Contacts.route.name) {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}