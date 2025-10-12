package br.com.lucolimac.shesafe.android.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.LOGIN_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.homeScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.loginScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

internal const val LOGIN_GRAPH_ROUTE = "loginGraph"
fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    helpRequestViewModel: HelpRequestViewModel,
) {
    navigation(startDestination = LOGIN_ROUTE, route = LOGIN_GRAPH_ROUTE) {
        loginScreen(navController)
        homeScreen(
            navController,
            homeViewModel,
            secureContactViewModel,
            authViewModel,
            settingsViewModel,
            profileViewModel,
            helpRequestViewModel
        )
    }
}

fun NavHostController.navigateToLoginGraph() {
    navigate(LOGIN_GRAPH_ROUTE) {
        popUpTo(LOGIN_GRAPH_ROUTE) {
            inclusive = true
        }
    }
}