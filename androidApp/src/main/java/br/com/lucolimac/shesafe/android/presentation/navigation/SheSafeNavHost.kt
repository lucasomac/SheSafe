package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.HOME_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.PROFILE_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.SECURE_CONTACTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.navigateToHome
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.navigateToProfile
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.navigateToSecureContacts
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.helpRequestsGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.homeGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.loginGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.profileGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.registerSecureContactGraph
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.secureContactsGraph
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.RegisterSecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel


@Composable
fun SheSafeNavHost(
    navController: NavHostController,
    startDestination: String,
    profileViewModel: ProfileViewModel,
    homeViewModel: HomeViewModel,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    registerSecureContactViewModel: RegisterSecureContactViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        loginGraph(
            navController,
            homeViewModel,
            secureContactViewModel,
            authViewModel,
            settingsViewModel,
            profileViewModel,
            helpRequestViewModel
        )
        homeGraph(
            navController,
            homeViewModel,
            secureContactViewModel,
            authViewModel,
            helpRequestViewModel,
            settingsViewModel,
            profileViewModel
        )
        secureContactsGraph(
            navController,
            secureContactViewModel,
            authViewModel,
            helpRequestViewModel,
            settingsViewModel,
            homeViewModel,
            profileViewModel,
            registerSecureContactViewModel
        )
        profileGraph(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            homeViewModel,
            navController,
            secureContactViewModel,
            profileViewModel
        )
        registerSecureContactGraph(
            secureContactViewModel, registerSecureContactViewModel, navController
        )

        helpRequestsGraph(
            profileViewModel,
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