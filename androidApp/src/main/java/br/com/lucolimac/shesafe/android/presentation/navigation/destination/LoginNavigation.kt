package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.LoginScreen

const val LOGIN_ROUTE = "login"
fun NavGraphBuilder.loginScreen(navController: NavHostController) {
    composable(LOGIN_ROUTE) { LoginScreen(navController) }
}

fun NavHostController.navigateToLogin() {
    this.navigate(LOGIN_ROUTE) {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}