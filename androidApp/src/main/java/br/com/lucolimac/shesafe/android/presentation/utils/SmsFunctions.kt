package br.com.lucolimac.shesafe.android.presentation.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

object SmsFunctions {
    fun sendSmsWithCallback(
        context: Context,
        phoneNumbers: List<String>,
        message: String,
        onResult: (sent: Boolean, delivered: Boolean) -> Unit
    ) {
        var sentResult: Boolean? = null
        var deliveredResult: Boolean? = null

        val sentReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                sentResult = resultCode == Activity.RESULT_OK
                if (deliveredResult != null) {
                    onResult(sentResult ?: false, deliveredResult ?: false)
                    context.unregisterReceiver(this)
                    Log.d("SMS_SENT", "SMS enviado com sucesso!")
                }
            }
        }
        val deliveredReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                deliveredResult = resultCode == Activity.RESULT_OK
                if (sentResult != null) {
                    onResult(sentResult ?: false, deliveredResult ?: false)
                    context.unregisterReceiver(this)
                    Log.d("SMS_DELIVERED", "SMS recebido com sucesso!")
                }
            }
        }

        ContextCompat.registerReceiver(
            context, sentReceiver, IntentFilter("SMS_SENT"), ContextCompat.RECEIVER_NOT_EXPORTED
        )
        ContextCompat.registerReceiver(
            context,
            deliveredReceiver,
            IntentFilter("SMS_DELIVERED"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        val sentIntent = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE
        )
        val deliveredIntent = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_DELIVERED"), PendingIntent.FLAG_IMMUTABLE
        )
        val smsManager = ContextCompat.getSystemService(context, SmsManager::class.java)
        val partsList = smsManager?.divideMessage(message) ?: arrayListOf(message)

        // Build lists with one PendingIntent per message part
        val sentIntents = ArrayList<PendingIntent?>()
        val deliveredIntents = ArrayList<PendingIntent?>()
        for (i in 0 until partsList.size) {
            sentIntents.add(sentIntent)
            deliveredIntents.add(deliveredIntent)
        }
        phoneNumbers.forEach {
            smsManager?.sendMultipartTextMessage(it, null, partsList, sentIntents, deliveredIntents)
        }
    }

    fun sendSmsIntent(context: Context, phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri() // Apenas apps de SMS devem lidar com isso
            putExtra("sms_body", message)
        }
        // Verifica se há um aplicativo para lidar com a intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Informa ao usuário que não há aplicativo de SMS disponível
            Toast.makeText(context, "Nenhum aplicativo de SMS encontrado.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}