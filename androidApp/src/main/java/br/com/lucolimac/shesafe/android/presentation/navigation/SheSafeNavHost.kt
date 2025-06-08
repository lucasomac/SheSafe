package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel


@Composable
fun SheSafeNavHost(
    navController: NavHostController,
    startDestination: String,
    innerPadding: PaddingValues,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        loginScreen(navController)
        homeScreen(navController, secureContactViewModel, authViewModel)
        secureContactsScreen(navController, secureContactViewModel)
        profileScreen(
            helpRequestViewModel,
            settingsViewModel,
            authViewModel,
            navController,
            secureContactViewModel
        )
        registerSecureContactScreen(secureContactViewModel, navController)
        helpRequestsScreen(helpRequestViewModel)
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