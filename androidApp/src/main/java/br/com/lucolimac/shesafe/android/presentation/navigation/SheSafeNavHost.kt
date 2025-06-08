package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import br.com.lucolimac.shesafe.android.presentation.model.NavigationItem
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

val BottomBarItems = listOf(
    NavigationItem.SecureContacts, NavigationItem.Home, NavigationItem.Profile
)

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