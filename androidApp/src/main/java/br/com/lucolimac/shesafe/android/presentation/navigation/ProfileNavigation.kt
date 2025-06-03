package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.ProfileScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import br.com.lucolimac.shesafe.route.SheSafeDestination

fun NavGraphBuilder.profileScreen(
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel
) {
    composable(SheSafeDestination.Profile.route.name) {
        ProfileScreen(
            helpRequestViewModel, settingsViewModel, authViewModel,
            onHelpRequestsShowClick = navController::navigateToHelpRequests,
            onLogoutClick = {
                // Clear the secure contact list
                secureContactViewModel.resetAllStates()
                // Clear the help request list
                helpRequestViewModel.resetAllStates()
                // Clear the settings
                settingsViewModel.resetAllStates()
                // Clear the auth state
                authViewModel.resetAllStates()
                // Logout the user
                authViewModel.logoutUser()
                // Navigate to the login screen
                navController.navigateToLogin()
            },
        )
    }
}

fun NavHostController.navigateToProfile() {
    this.navigate(SheSafeDestination.Profile.route.name) {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}