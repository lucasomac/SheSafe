package br.com.lucolimac.shesafe.android.presentation.component.geolocation

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@SuppressLint("MissingPermission")
@Composable
fun rememberLocation(): Location? {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    LaunchedEffect(key1 = Unit) {
        val cancellationTokenSource = CancellationTokenSource()
        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token
        ).addOnSuccessListener { loc: Location? ->
            location = loc
        }
    }
    return location
}