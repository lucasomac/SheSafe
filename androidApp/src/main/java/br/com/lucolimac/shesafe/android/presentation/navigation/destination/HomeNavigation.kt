package br.com.lucolimac.shesafe.android.presentation.navigation.destination

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel

// Utility to send SMS and handle callbacks
fun sendSmsWithCallback(
    context: Context,
    phoneNumber: String,
    message: String,
    onResult: (sent: Boolean, delivered: Boolean) -> Unit
) {
    var sentResult: Boolean? = null
    var deliveredResult: Boolean? = null

    val sentReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            sentResult = resultCode == Activity.RESULT_OK
            if (deliveredResult != null) {
                onResult(sentResult, deliveredResult!!)
                context.unregisterReceiver(this)
                Log.d("SMS_SENT", "SMS enviado com sucesso!")
            }
        }
    }
    val deliveredReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            deliveredResult = resultCode == Activity.RESULT_OK
            if (sentResult != null) {
                onResult(sentResult, deliveredResult)
                context.unregisterReceiver(this)
                Log.d("SMS_DELIVERED", "SMS recebido com sucesso!")
            }
        }
    }
    ContextCompat.registerReceiver(
        context, sentReceiver, IntentFilter("SMS_SENT"), ContextCompat.RECEIVER_NOT_EXPORTED
    )
    ContextCompat.registerReceiver(
        context,
        deliveredReceiver,
        IntentFilter("SMS_DELIVERED"),
        ContextCompat.RECEIVER_NOT_EXPORTED
    )

    val sentIntent = PendingIntent.getBroadcast(
        context, 0, Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE
    )
    val deliveredIntent = PendingIntent.getBroadcast(
        context, 0, Intent("SMS_DELIVERED"), PendingIntent.FLAG_IMMUTABLE
    )
    val smsManager = ContextCompat.getSystemService(context, SmsManager::class.java)
    smsManager?.sendTextMessage(phoneNumber, null, message, sentIntent, deliveredIntent)
}

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
    composable(HOME_ROUTE) {
        var smsSent by remember { mutableStateOf<Boolean?>(null) }
//        var smsDelivered by remember { mutableStateOf<Boolean?>(null) }
//        var smsStatus by remember { mutableStateOf<Pair<Boolean?, Boolean?>?>(null) }

        HomeScreen(
            homeViewModel = homeViewModel,
            secureContactViewModel = secureContactViewModel,
            authViewModel = authViewModel,
            settingsViewModel = settingsViewModel,
            profileViewModel = profileViewModel,
            onOrderHelp = { helpRequest, message, context ->
                try {
                    sendSmsWithCallback(
                        context, helpRequest.phoneNumber, message
                    ) { sent, delivered ->
                        // Atualiza o estado com o resultado do SMS
//                        smsStatus = Pair(sent, delivered)
                        smsSent = sent
//                        smsDelivered = delivered
                        if (sent) helpRequestViewModel.registerHelpRequest(helpRequest)
                    }
                } catch (_: Exception) {
                    // Em caso de erro, define ambos como false
//                    smsStatus = Pair(false, false)
                    smsSent = false
//                    smsDelivered = false
                }
                // Não retorna Pair, resultado é tratado via estado
            },
            onNoContacts = navController::navigateToSecureContacts,
            smsStatus = smsSent
        )
    }
}

fun NavHostController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HOME_ROUTE, navOptions)
}