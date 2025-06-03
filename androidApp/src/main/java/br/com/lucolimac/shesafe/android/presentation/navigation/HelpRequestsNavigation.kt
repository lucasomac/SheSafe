package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.HelpRequestsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.route.SheSafeDestination

fun NavGraphBuilder.helpRequestsScreen(helpRequestViewModel: HelpRequestViewModel) {
    composable(SheSafeDestination.HelpRequests.route.name) {
        HelpRequestsScreen(
            helpRequestViewModel = helpRequestViewModel
        )
    }
}
fun NavHostController.navigateToHelpRequests() {
    this.navigate(SheSafeDestination.HelpRequests.route.name) {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}