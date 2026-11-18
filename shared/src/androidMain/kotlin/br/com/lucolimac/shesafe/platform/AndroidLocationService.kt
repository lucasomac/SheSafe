package br.com.lucolimac.shesafe.platform

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Android implementation of [LocationService] powered by Google Play Services' Fused Location Provider.
 *
 * @property context Android [Context] used to verify location permissions and obtain the fused location client.
 */
class AndroidLocationService(
    private val context: Context,
) : LocationService {
    /**
     * Queries a high-accuracy single-shot device location fix after verifying runtime location permissions.
     *
     * Immediately reports [LocationResult.PermissionRequired] when neither fine nor coarse location
     * permission is granted.
     *
     * @param onResult Callback invoked with [LocationResult.Available], [LocationResult.Unavailable],
     *   [LocationResult.PermissionRequired], or [LocationResult.Failed].
     */
    @SuppressLint("MissingPermission")
    override fun requestCurrentLocation(onResult: (LocationResult) -> Unit) {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFinePermission && !hasCoarsePermission) {
            onResult(LocationResult.PermissionRequired)
            return
        }

        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token,
            )
            .addOnSuccessListener { location ->
                onResult(
                    location?.let {
                        LocationResult.Available(
                            LocationCoordinates(it.latitude, it.longitude),
                        )
                    } ?: LocationResult.Unavailable,
                )
            }
            .addOnFailureListener { exception ->
                onResult(LocationResult.Failed(exception.message ?: "Unable to read location"))
            }
    }
}
