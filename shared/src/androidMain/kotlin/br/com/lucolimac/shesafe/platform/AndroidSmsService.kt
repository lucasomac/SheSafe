package br.com.lucolimac.shesafe.platform

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

/**
 * Android implementation of [SmsService] backed by the platform [SmsManager] system service.
 *
 * @property context Android [Context] used to inspect telephony capabilities, check `SEND_SMS`
 *   permission, and resolve [SmsManager].
 */
class AndroidSmsService(
    private val context: Context,
) : SmsService {
    /**
     * Sends the emergency SMS message directly to all [SmsRequest.recipients] when telephony hardware
     * and `SEND_SMS` runtime permission are available.
     *
     * Reports [SmsResult.Unsupported] on devices without messaging hardware and
     * [SmsResult.UserConfirmationRequired] when `SEND_SMS` permission has not been granted.
     *
     * @param request [SmsRequest] containing recipient phone numbers and the composed message body.
     * @param onResult Callback invoked with [SmsResult.Sent], [SmsResult.UserConfirmationRequired],
     *   [SmsResult.Unsupported], or [SmsResult.Failed].
     */
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
