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
import br.com.lucolimac.shesafe.android.R
import br.com.lucolimac.shesafe.android.domain.entity.OrderHelp
import br.com.lucolimac.shesafe.android.presentation.actions.ScreenAction
import br.com.lucolimac.shesafe.android.presentation.component.HomeHeader
import br.com.lucolimac.shesafe.android.presentation.component.SearchBar
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeDialog
import br.com.lucolimac.shesafe.android.presentation.component.button.RoundedButton
import br.com.lucolimac.shesafe.android.presentation.component.geolocation.LocationPermissionRequester
import br.com.lucolimac.shesafe.android.presentation.component.geolocation.rememberLocation
import br.com.lucolimac.shesafe.android.presentation.model.DialogModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
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
    secureContactViewModel: SecureContactViewModel,
    onOrderHelp: (String, String, Context) -> Boolean,
    modifier: Modifier = Modifier
) {
    val userLocation = rememberLocation()
    var showDialog by remember { mutableStateOf(false) }
    var doNotAskAgain by remember { mutableStateOf(false) }
    var dialogModel by remember { mutableStateOf<DialogModel?>(null) }
    val secureContacts by secureContactViewModel.secureContacts.collectAsState()
    var hasSecureContacts by remember { mutableStateOf(secureContacts.isEmpty()) }
    val smsPermissionState = rememberPermissionState(Manifest.permission.SEND_SMS)
    val context = LocalContext.current
    val screenAction = ScreenAction()
    val sendSms = {
        // Permission granted, proceed with sending SMS
        var countSmsSent = 0
        secureContacts.forEach {
            val orderHHelp = OrderHelp(
                phone = it.phoneNumber, location = LatLng(
                    userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0
                ), createdAt = LocalDateTime.now()
            )
            val hasBeenSent = onOrderHelp(
                orderHHelp.phone, orderHHelp.linkMap, context
            )
            if (hasBeenSent) {
                countSmsSent++
            }
        }
        if (countSmsSent > 0) {
            Toast.makeText(
                context, context.getString(
                    R.string.sms_sent, countSmsSent.toString()
                ), Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context, context.getString(R.string.sms_not_sent), Toast.LENGTH_SHORT
            ).show()
        }
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sendSms()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(
                context,
                "Permission to send SMS is required to use this feature.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LocationPermissionRequester {
        val cameraPositionState = rememberCameraPositionState {
            position = if (userLocation != null) {
                CameraPosition.fromLatLngZoom(
                    LatLng(userLocation.latitude, userLocation.longitude), 10f
                )
            } else {
                CameraPosition.fromLatLngZoom(LatLng(1.35, 103.87), 10f) // Default
            }
        }

        LaunchedEffect(key1 = userLocation) {
            if (userLocation != null) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(userLocation.latitude, userLocation.longitude), 16f
                    ),
                )
            }
        }
        Column {
            HomeHeader(
                stringResource(R.string.title_home),
                modifier = modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
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
                    ), // remove default buttons.
                    properties = MapProperties(isMyLocationEnabled = true)
                )

                // Search bar (placed second, on top of the map)
                SearchBar(
                    onSearch = { query ->
                        // Handle the search query (e.g., filter map markers)
                        println("Searching for: $query")
                    }, modifier = modifier
                        .align(Alignment.TopCenter) // Align at the top
                        .padding(16.dp) // Add padding
                )
                // Button (placed third, on top of the map)
                RoundedButton(
                    text = stringResource(R.string.request_help),
                    onClick = {
                        when (smsPermissionState.status) {
                            is PermissionStatus.Granted -> {
                                dialogModel =
                                    screenAction.chooseDialogModel(hasSecureContacts, onConfirm = {
                                        sendSms()
                                        showDialog = false
                                    }, onDismiss = {
                                        showDialog = false
                                    }, onCheckboxCheckedChange = { isChecked ->
                                        doNotAskAgain = isChecked
                                    })
                                showDialog = true
                            }

                            is PermissionStatus.Denied -> {
                                // Handle the case when permission is denied
                                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                            }
                        }
                    },
                    modifier = modifier
                        .align(Alignment.BottomCenter) // Align at the bottom center
                        .padding(16.dp) // Add some padding
                )
            }
        }
    }
    if (showDialog && dialogModel != null) {
        SheSafeDialog(
            dialogModel = dialogModel!!, onDismissRequest = {
                showDialog = false
            })
    }

}