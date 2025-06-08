package br.com.lucolimac.shesafe.android.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.HOME_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.homeScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.profileScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.secureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
internal const val HOME_GRAPH_ROUTE = "homeGraph"
fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel
) {
    navigation(
        startDestination = HOME_ROUTE, route = HOME_GRAPH_ROUTE
    ) {
        homeScreen(navController, secureContactViewModel, authViewModel)
        secureContactsScreen(navController, secureContactViewModel)
        profileScreen(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            navController,
            secureContactViewModel
        )
    }
}
fun NavHostController.navigateToHomeGraph() {
    navigate(HOME_GRAPH_ROUTE) {
        popUpTo(HOME_GRAPH_ROUTE) {
            inclusive = true
        }
    }
}