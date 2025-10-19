package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.state.SmsStatusState
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
        val smsStatusSent by helpRequestViewModel.smsStatusSent.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) {
            homeViewModel.getAllSecureContacts()
        }
        val context = LocalContext.current
        LaunchedEffect(smsStatusSent) {
            when (smsStatusSent) {
                is SmsStatusState.Result -> {
                    val res = smsStatusSent as SmsStatusState.Result
                    Toast.makeText(
                        context,
                        context.getString(R.string.sms_sent),
                        Toast.LENGTH_SHORT,
                    ).show()
                    helpRequestViewModel.resetSmsStatus()
                }

                else -> return@LaunchedEffect
            }
        }
        HomeScreen(
            homeViewModel = homeViewModel,
            secureContactViewModel = secureContactViewModel,
            authViewModel = authViewModel,
            settingsViewModel = settingsViewModel,
            profileViewModel = profileViewModel,
            onOrderHelp = { contacts, message, location ->
                helpRequestViewModel.sendSms(contacts, message, location)
            },
            onNoContacts = navController::navigateToSecureContacts,
        )
    }
}

fun NavHostController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HOME_ROUTE, navOptions)
}
