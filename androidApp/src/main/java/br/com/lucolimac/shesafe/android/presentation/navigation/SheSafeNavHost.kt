package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.HELP_REQUESTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.HOME_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.PROFILE_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.REGISTER_SECURE_CONTACT_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.SECURE_CONTACTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.helpRequestsScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.navigateToHome
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.navigateToProfile
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.navigateToSecureContacts
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.profileScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.registerSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.secureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.helpRequestsGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.homeGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.loginGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.profileGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.registerSecureContactGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.secureContactsGraph
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel


@Composable
fun SheSafeNavHost(
    navController: NavHostController,
    startDestination: String,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        loginGraph(navController, secureContactViewModel, authViewModel)
        homeGraph(
            navController,
            secureContactViewModel,
            authViewModel,
            helpRequestViewModel,
            settingsViewModel
        )
        secureContactsGraph(
            navController,
            secureContactViewModel,
            authViewModel,
            helpRequestViewModel,
            settingsViewModel
        )
        profileGraph(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            navController,
            secureContactViewModel
        )
        registerSecureContactGraph(secureContactViewModel, navController)

        helpRequestsGraph(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            navController,
            secureContactViewModel
        )
    }
}


fun NavHostController.navigateSingleTopWithPopUpTo(
    item: NavigationItem
) {
    val (route, navigateTo) = when (item) {
        NavigationItem.SecureContacts -> Pair(
            SECURE_CONTACTS_ROUTE, ::navigateToSecureContacts
        )

        NavigationItem.Home -> Pair(
            HOME_ROUTE, ::navigateToHome
        )

        NavigationItem.Profile -> Pair(
            PROFILE_ROUTE, ::navigateToProfile
        )

    }
    val navOptions = navOptions {
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
        popUpTo(route)
    }
    navigateTo(navOptions)
}