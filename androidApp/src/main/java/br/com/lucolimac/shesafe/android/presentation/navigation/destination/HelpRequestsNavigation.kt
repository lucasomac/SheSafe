package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.HelpRequestsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel

const val HELP_REQUESTS_ROUTE = "helprequests"
fun NavGraphBuilder.helpRequestsScreen(helpRequestViewModel: HelpRequestViewModel) {
    composable(HELP_REQUESTS_ROUTE) {
        HelpRequestsScreen(
            helpRequestViewModel = helpRequestViewModel
        )
    }
}

fun NavHostController.navigateToHelpRequests() {
    this.navigate(HELP_REQUESTS_ROUTE) {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}