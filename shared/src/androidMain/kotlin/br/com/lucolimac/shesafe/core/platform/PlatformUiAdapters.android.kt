package br.com.lucolimac.shesafe.core.platform

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.lucolimac.shesafe.core.logging.logWarn
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

private const val DEFAULT_BR_LAT = -15.793889
private const val DEFAULT_BR_LNG = -47.882778
private const val INITIAL_ZOOM = 4f
private const val FOCUSED_ZOOM = 16f

@Composable
actual fun EmergencyMapView(
    userLocation: LocationCoordinates?,
    hasLocationPermission: Boolean,
    modifier: Modifier,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = if (userLocation != null) {
            CameraPosition.fromLatLngZoom(
                LatLng(userLocation.latitude, userLocation.longitude),
                FOCUSED_ZOOM,
            )
        } else {
            CameraPosition.fromLatLngZoom(LatLng(DEFAULT_BR_LAT, DEFAULT_BR_LNG), INITIAL_ZOOM)
        }
    }

    var mapLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(userLocation, mapLoaded) {
        if (userLocation != null && mapLoaded) {
            runCatching {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(userLocation.latitude, userLocation.longitude),
                        FOCUSED_ZOOM,
                    ),
                )
            }.onFailure { error ->
                logWarn("EmergencyMapView", "Failed to animate GoogleMap camera: ${error.message}")
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            compassEnabled = false,
            zoomControlsEnabled = true,
            mapToolbarEnabled = false,
        ),
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        onMapLoaded = { mapLoaded = true },
    )
}

@Composable
actual fun rememberLocationPermissionControl(
    onPermissionResult: (Boolean) -> Unit,
): LocationPermissionControl {
    val context = LocalContext.current

    fun checkGranted(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    var isGranted by remember { mutableStateOf(checkGranted()) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isGranted = checkGranted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        isGranted = granted
        onPermissionResult(granted)
    }

    return remember(isGranted, launcher) {
        LocationPermissionControl(
            isGranted = isGranted,
            requestPermission = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                )
            },
        )
    }
}
