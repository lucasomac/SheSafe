package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
//        val secureContactViewModel: SecureContactViewModel by inject(SecureContactViewModel::class.java)
        val uiState = secureContactViewModel.uiState.collectAsState().value
        LaunchedEffect(Unit) {
            secureContactViewModel.getAllSecureContacts()
        }
        SecureContactsScreen(
            onEditAction = { secureContact ->
            navController.navigateToRegisterSecureContact(secureContact.phoneNumber)
        }, onDeleteAction = {
            secureContactViewModel.deleteSecureContact(it.phoneNumber)
        }, uiState
        )
    }
}

fun NavHostController.navigateToSecureContacts(navOptions: NavOptions? = null) {
    navigate(SECURE_CONTACTS_ROUTE, navOptions)
}