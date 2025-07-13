package br.com.lucolimac.shesafe.android.presentation.screen

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import br.com.lucolimac.shesafe.android.presentation.actions.ScreenAction
import br.com.lucolimac.shesafe.android.presentation.component.HomeHeader
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeDialog
import br.com.lucolimac.shesafe.android.presentation.component.button.RoundedButton
import br.com.lucolimac.shesafe.android.presentation.component.geolocation.LocationPermissionRequester
import br.com.lucolimac.shesafe.android.presentation.component.geolocation.rememberLocation
import br.com.lucolimac.shesafe.android.presentation.model.DialogModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import br.com.lucolimac.shesafe.enum.SettingsEnum
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDateTime

/**
 * Composable function for the home screen of the application.
 *
 * This screen displays a map with the user's current location and allows the user to
 * request help by clicking a button.  It handles location permissions and updates the
 * map based on the device's location.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    secureContactViewModel: SecureContactViewModel,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    onOrderHelp: (HelpRequest, String, Context) -> Boolean,
    onNoContacts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) { authViewModel.setFieldsOfLoggedUser() }
    val userLocation = rememberLocation()
    var showDialog by remember { mutableStateOf(false) }
    var isShowSendConfirmation by remember { mutableStateOf(settingsViewModel.mapOfSettings.value[SettingsEnum.SEND_CONFIRMATION]) }
    var dialogModel by remember { mutableStateOf<DialogModel?>(null) }
    val secureContacts = secureContactViewModel.uiState.collectAsState().value.secureContacts
    val hasSecureContacts =
        secureContactViewModel.uiState.collectAsState().value.secureContacts.isNotEmpty()
    val smsPermissionState = rememberPermissionState(Manifest.permission.SEND_SMS)
    val context = LocalContext.current
    val screenAction = ScreenAction()
    val userMessage = profileViewModel.helpMessage.collectAsState()
    LaunchedEffect(Unit) {
        secureContactViewModel.getAllSecureContacts()
    }
    val sendSms = {
        // Permission granted, proceed with sending SMS
        var countSmsSent = 0
        secureContacts.forEach { secureContact ->
            val orderHHelp = HelpRequest(
                phoneNumber = secureContact.phoneNumber,
                location = LatLng(
                    userLocation?.latitude ?: 0.0,
                    userLocation?.longitude ?: 0.0,
                ),
                createdAt = LocalDateTime.now(),
            )
            val messageFormatedWithLocation =
                "${userMessage.takeIf { it.value.isNotEmpty() }?.value ?: context.getString(R.string.default_message_danger_user)} \nhttps://www.google.com/maps/search/?api=1&query=${orderHHelp.location.latitude},${orderHHelp.location.longitude}"
            val hasBeenSent = onOrderHelp(
                orderHHelp,
                messageFormatedWithLocation,
                context,
            )
            if (hasBeenSent) {
                countSmsSent++
            }
        }
        if (countSmsSent > 0) {
            Toast.makeText(
                context,
                context.getString(
                    R.string.sms_sent,
                    countSmsSent.toString(),
                ),
                Toast.LENGTH_SHORT,
            ).show()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.sms_not_sent),
                Toast.LENGTH_SHORT,
            ).show()
        }
        if (homeViewModel.isCheckboxChecked.value != isShowSendConfirmation) {
            settingsViewModel.setToggleSetting(
                SettingsEnum.SEND_CONFIRMATION, homeViewModel.isCheckboxChecked.value
            )
        }
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            sendSms()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(
                context,
                "Permission to send SMS is required to use this feature.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    LocationPermissionRequester {
        val cameraPositionState = rememberCameraPositionState {
            position = if (userLocation != null) {
                CameraPosition.fromLatLngZoom(
                    LatLng(userLocation.latitude, userLocation.longitude),
                    10F,
                )
            } else {
                CameraPosition.fromLatLngZoom(LatLng(1.35, 103.87), 10f) // Default
            }
        }

        LaunchedEffect(key1 = userLocation) {
            if (userLocation != null) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(userLocation.latitude, userLocation.longitude),
                        16f,
                    ),
                )
            }
        }
        Column {
            HomeHeader(
                stringResource(R.string.title_home),
                modifier = modifier.align(Alignment.CenterHorizontally),
            )
            Box(modifier = modifier.fillMaxSize()) {
                // Map (placed first, so it's in the background)
                GoogleMap(
                    modifier = modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        compassEnabled = false,
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = false,
                    ),
                    properties = MapProperties(isMyLocationEnabled = true),
                )
                RoundedButton(
                    text = stringResource(R.string.request_help),
                    onClick = {
                        when (smsPermissionState.status) {
                            is PermissionStatus.Granted -> {
                                if (hasSecureContacts) {
                                    if (isShowSendConfirmation == true) {
                                        dialogModel = screenAction.chooseDialogModel(
                                            true,
                                            onConfirm = {
                                                sendSms()
                                                showDialog = false
                                            },
                                            onDismiss = {
                                                showDialog = false
                                            },
                                            showCheckbox = true,
                                            onCheckboxCheckedChange = { isChecked ->
                                                homeViewModel.setCheckboxChecked(isChecked)
                                            })
                                        showDialog = true
                                    } else {
                                        sendSms()
                                    }
                                } else {
                                    dialogModel = screenAction.chooseDialogModel(
                                        false,
                                        onConfirm = {
                                            onNoContacts()
                                            showDialog = false
                                        },
                                        onDismiss = {
                                            showDialog = false
                                        },
                                        showCheckbox = false,
                                    )
                                    showDialog = true
                                }
                            }

                            is PermissionStatus.Denied -> {
                                // Handle the case when permission is denied
                                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                            }
                        }
                    },
                    modifier = modifier
                        .align(Alignment.BottomCenter) // Align at the bottom center
                        .padding(16.dp),
                    // Add some padding
                )
            }
        }
    }
    if (showDialog && dialogModel != null) {
        SheSafeDialog(
            dialogModel = dialogModel!!,
            onDismissRequest = {
                showDialog = false
            },
        )
    }
}
