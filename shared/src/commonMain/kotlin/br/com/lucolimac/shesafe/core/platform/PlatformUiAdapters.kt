package br.com.lucolimac.shesafe.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.lucolimac.shesafe.platform.LocationCoordinates

@Composable
expect fun EmergencyMapView(
    userLocation: LocationCoordinates?,
    hasLocationPermission: Boolean,
    modifier: Modifier = Modifier,
)

data class LocationPermissionControl(
    val isGranted: Boolean,
    val requestPermission: () -> Unit,
)

@Composable
expect fun rememberLocationPermissionControl(
    onPermissionResult: (Boolean) -> Unit = {},
): LocationPermissionControl
