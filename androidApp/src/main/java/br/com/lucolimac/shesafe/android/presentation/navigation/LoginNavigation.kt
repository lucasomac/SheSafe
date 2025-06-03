package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.LoginScreen
import br.com.lucolimac.shesafe.route.SheSafeDestination

fun NavGraphBuilder.loginScreen(navController: NavHostController) {
    composable(SheSafeDestination.Login.route.name) { LoginScreen(navController) }
}
fun NavHostController.navigateToLogin() {
    this.navigate(SheSafeDestination.Login.route.name) {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}