package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.SecureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel

const val SECURE_CONTACTS_ROUTE = "securecontacts"
fun NavGraphBuilder.secureContactsScreen(
    navController: NavHostController, secureContactViewModel: SecureContactViewModel
) {
    composable(SECURE_CONTACTS_ROUTE) {
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

fun NavHostController.navigateToSecureContacts(navOptions: NavOptions? = null) {
    navigate(SECURE_CONTACTS_ROUTE, navOptions)
}