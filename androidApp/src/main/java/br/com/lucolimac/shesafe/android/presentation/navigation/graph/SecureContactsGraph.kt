package br.com.lucolimac.shesafe.android.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.SECURE_CONTACTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.homeScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.profileScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.registerSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.secureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

internal const val SECURE_CONTACTS_GRAPH_ROUTE = "secureContactsGraph"
fun NavGraphBuilder.secureContactsGraph(
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel
) {
    navigation(startDestination = SECURE_CONTACTS_ROUTE, route = SECURE_CONTACTS_GRAPH_ROUTE) {
        secureContactsScreen(navController, secureContactViewModel)
        registerSecureContactScreen(secureContactViewModel, navController)
        homeScreen(navController, secureContactViewModel, authViewModel)
        profileScreen(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            navController,
            secureContactViewModel
        )
    }
}
fun NavHostController.navigateToSecureContactsGraph() {
    navigate(SECURE_CONTACTS_GRAPH_ROUTE) {
        popUpTo(SECURE_CONTACTS_GRAPH_ROUTE) {
            inclusive = true
        }
    }
}