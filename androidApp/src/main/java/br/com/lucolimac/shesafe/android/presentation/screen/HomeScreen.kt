package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.android.R
import br.com.lucolimac.shesafe.android.presentation.component.HomeHeader
import br.com.lucolimac.shesafe.android.presentation.component.RoundedButton
import br.com.lucolimac.shesafe.android.presentation.component.SearchBar
import br.com.lucolimac.shesafe.android.presentation.component.geolocation.LocationPermissionRequester
import br.com.lucolimac.shesafe.android.presentation.component.geolocation.rememberLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
@Composable
fun HomeScreen() {
    val userLocation = rememberLocation()
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
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Box(modifier = Modifier.fillMaxSize()) {
                // Map (placed first, so it's in the background)
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
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
                    }, modifier = Modifier
                        .align(Alignment.TopCenter) // Align at the top
                        .padding(16.dp) // Add padding
                )
                // Button (placed third, on top of the map)
                RoundedButton(
                    text = stringResource(R.string.request_help),
                    onClick = {
                        TODO("Implement click action")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter) // Align at the bottom center
                        .padding(16.dp) // Add some padding
                )
            }
        }
    }
}