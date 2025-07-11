package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

const val HOME_ROUTE = "home"
fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
) {
    composable(HOME_ROUTE) {
        HomeScreen(
            homeViewModel = homeViewModel,
            secureContactViewModel = secureContactViewModel,
            authViewModel = authViewModel,
            settingsViewModel = settingsViewModel,
            onOrderHelp = { message, phoneNumber, context ->
                try {
                    val smsManager = ContextCompat.getSystemService<SmsManager>(
                        context,
                        SmsManager::class.java,
                    )
                    smsManager?.sendTextMessage(phoneNumber, null, message, null, null)
                    true
                } catch (_: Exception) {
                    false
                }
            },
            onNoContacts = navController::navigateToSecureContacts,
        )
    }
}

fun NavHostController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HOME_ROUTE, navOptions)
}