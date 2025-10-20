package br.com.lucolimac.shesafe.android.presentation.screen

import android.Manifest
import android.util.Log
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
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
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
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

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
    onOrderHelp: (List<SecureContact>, String, GeoPoint) -> Unit,
    onNoContacts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        authViewModel.setFieldsOfLoggedUser()
        secureContactViewModel.getAllSecureContacts()
        profileViewModel.getHelpMessages()
    }
    val context = LocalContext.current
    val userLocation = rememberLocation()
    val cameraPositionState = rememberCameraPositionState {
        position = if (userLocation != null) {
            CameraPosition.fromLatLngZoom(
                LatLng(userLocation.latitude, userLocation.longitude),
                10F,
            )
        } else {
            CameraPosition.fromLatLngZoom(LatLng(1.35, 103.87), 10f)
        }
    }

    var mapLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(userLocation, mapLoaded) {
        if (userLocation != null && mapLoaded) {
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(userLocation.latitude, userLocation.longitude),
                        16f,
                    ),
                )
                Log.d(
                    "MAP",
                    "Camera atualizada para: ${userLocation.latitude},${userLocation.longitude}"
                )
            } catch (e: Exception) {
                Log.e("MAP", "Erro ao atualizar camera: ${e.message}")
            }
        }
    }
    // Estados para diálogo e configurações
    var showDialog by remember { mutableStateOf(false) }
    var isShowSendConfirmation by remember { mutableStateOf(settingsViewModel.mapOfSettings.value[SettingsEnum.SEND_CONFIRMATION]) }
    var dialogModel by remember { mutableStateOf<DialogModel?>(null) }
    val secureContacts by homeViewModel.secureContacts.collectAsState()
    val hasSecureContacts = secureContacts.isNotEmpty()
    val smsPermissionState = rememberPermissionState(Manifest.permission.SEND_SMS)
    val screenAction = ScreenAction()
    val userMessage by profileViewModel.helpMessage.collectAsState()

    val sendSms = {
        val messageFormatedWithLocation =
            "${userMessage.takeIf { it.isNotEmpty() } ?: context.getString(R.string.default_message_danger_user)} https://www.google.com/maps/search/?api=1&query=${userLocation?.latitude ?: 0.0},${userLocation?.longitude ?: 0.0}"
        onOrderHelp(
            secureContacts, messageFormatedWithLocation, GeoPoint(
                userLocation?.latitude ?: 0.0,
                userLocation?.longitude ?: 0.0,
            )
        )
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
                "O aplicativo requer permissão para enviar SMS.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
    LocationPermissionRequester {
        Column {
            HomeHeader(
                stringResource(R.string.title_home),
                modifier = modifier.align(Alignment.CenterHorizontally),
            )
            Box(modifier = modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        compassEnabled = false,
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = false,
                    ),
                    properties = MapProperties(isMyLocationEnabled = true),
                    onMapLoaded = {
                        mapLoaded = true
                        Log.d("MAP", "Mapa carregado com sucesso!")
                    })

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
                                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                            }
                        }
                    },
                    modifier = modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
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
