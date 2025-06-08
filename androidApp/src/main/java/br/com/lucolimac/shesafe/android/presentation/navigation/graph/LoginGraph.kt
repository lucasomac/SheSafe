package br.com.lucolimac.shesafe.android.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.LOGIN_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.homeScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.loginScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel

internal const val LOGIN_GRAPH_ROUTE = "loginGraph"
fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = LOGIN_ROUTE, route = LOGIN_GRAPH_ROUTE) {
        loginScreen(navController)
        homeScreen(
            navController, secureContactViewModel, authViewModel
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