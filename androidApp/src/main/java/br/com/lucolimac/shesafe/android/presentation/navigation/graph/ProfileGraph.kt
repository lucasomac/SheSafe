package br.com.lucolimac.shesafe.android.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.PROFILE_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.helpRequestsScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.homeScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.loginScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.profileScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.secureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

internal const val PROFILE_GRAPH_ROUTE = "profileGraph"
fun NavGraphBuilder.profileGraph(
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel
) {
    navigation(startDestination = PROFILE_ROUTE, route = PROFILE_GRAPH_ROUTE) {
        profileScreen(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            navController,
            secureContactViewModel
        )
        secureContactsScreen(navController, secureContactViewModel)
        homeScreen(
            navController,
            homeViewModel,
            secureContactViewModel,
            authViewModel,
            settingsViewModel
        )
        helpRequestsScreen(helpRequestViewModel)
        loginScreen(navController)
    }
}

fun NavHostController.navigateToProfileGraph() {
    navigate(PROFILE_GRAPH_ROUTE) {
        popUpTo(PROFILE_GRAPH_ROUTE) {
            inclusive = true
        }
    }
}