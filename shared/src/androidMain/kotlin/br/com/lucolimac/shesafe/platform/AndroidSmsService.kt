package br.com.lucolimac.shesafe.platform

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

class AndroidSmsService(
    private val context: Context,
) : SmsService {
    override fun send(request: SmsRequest, onResult: (SmsResult) -> Unit) {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_MESSAGING)) {
            onResult(SmsResult.Unsupported)
            return
        }
        if (
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.SEND_SMS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onResult(SmsResult.UserConfirmationRequired)
            return
        }

        runCatching {
            val smsManager = context.getSystemService(SmsManager::class.java)
            request.recipients.forEach { recipient ->
                smsManager.sendTextMessage(recipient, null, request.message, null, null)
            }
        }.onSuccess {
            onResult(SmsResult.Sent)
        }.onFailure { exception ->
            onResult(SmsResult.Failed(exception.message ?: "Unable to send SMS"))
        }
    }
}
