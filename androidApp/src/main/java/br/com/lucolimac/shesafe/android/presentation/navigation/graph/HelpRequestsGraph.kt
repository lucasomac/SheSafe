package br.com.lucolimac.shesafe.android.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.HELP_REQUESTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.helpRequestsScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.profileScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

internal const val HELP_REQUESTS_GRAPH_ROUTE = "helpRequestsGraph"
fun NavGraphBuilder.helpRequestsGraph(
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel
) {
    navigation(startDestination = HELP_REQUESTS_ROUTE, route = HELP_REQUESTS_GRAPH_ROUTE) {
        helpRequestsScreen(helpRequestViewModel)
        profileScreen(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            navController,
            secureContactViewModel
        )
    }
}

fun NavHostController.navigateToHelpRequestsGraph() {
    navigate(HELP_REQUESTS_GRAPH_ROUTE) {
        popUpTo(HELP_REQUESTS_GRAPH_ROUTE) {
            inclusive = true
        }
    }
}