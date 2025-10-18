package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.utils.SmsFunctions.sendSmsWithCallback
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

const val HOME_ROUTE = "home"
fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    helpRequestViewModel: HelpRequestViewModel
) {
    homeViewModel.getAllSecureContacts()
    composable(HOME_ROUTE) {
        var smsSentCount by remember { mutableIntStateOf(0) }
        var smsDeliveredCount by remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            homeViewModel.getAllSecureContacts()
        }
        HomeScreen(
            homeViewModel = homeViewModel,
            secureContactViewModel = secureContactViewModel,
            authViewModel = authViewModel,
            settingsViewModel = settingsViewModel,
            profileViewModel = profileViewModel,
            onOrderHelp = { helpRequest, message, context ->
                try {
                    sendSmsWithCallback(
                        context, helpRequest.phoneNumbers, message
                    ) { sent, delivered ->
                        smsSentCount = if (sent) smsSentCount++ else smsSentCount
                        smsDeliveredCount = if (delivered) smsDeliveredCount++ else smsSentCount
                    }
                    helpRequestViewModel.registerHelpRequest(helpRequest)
                } catch (_: Exception) {
                    smsSentCount = 0
                    smsDeliveredCount = 0
                }
            },
            onNoContacts = navController::navigateToSecureContacts,
        )
    }
}

fun NavHostController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HOME_ROUTE, navOptions)
}
