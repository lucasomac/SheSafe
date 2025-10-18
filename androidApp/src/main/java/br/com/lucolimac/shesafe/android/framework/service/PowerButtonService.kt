package br.com.lucolimac.shesafe.android.framework.service

import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PowerButtonService : Service() {

    private val helpRequestUseCase: HelpRequestUseCase by inject()
    private val secureContactUseCase: SecureContactUseCase by inject()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler(Looper.getMainLooper())
    private var clickCount = 0
    private val clickRunnable = Runnable { resetClickCount() }

    private val powerButtonReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                handlePowerButtonClick()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        registerReceiver(powerButtonReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(powerButtonReceiver)
    }

    private fun handlePowerButtonClick() {
        clickCount++
        handler.removeCallbacks(clickRunnable)
        if (clickCount == 3) {
            sendHelpRequest()
            resetClickCount()
        } else {
            handler.postDelayed(clickRunnable, 2000) // 2 segundos de intervalo
        }
    }

    private fun sendHelpRequest() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geoPoint = GeoPoint(it.latitude, it.longitude)
                CoroutineScope(Dispatchers.IO).launch {
                    secureContactUseCase.getSecureContacts().collect { contacts ->
                            helpRequestUseCase.registerHelpRequest(
                                HelpRequest(
                                    contacts.map { it.phoneNumber },
                                    geoPoint,
                                    Timestamp.now()
                                )
                            )
                    }
                }
            }
        }
    }

    private fun resetClickCount() {
        clickCount = 0
    }
}