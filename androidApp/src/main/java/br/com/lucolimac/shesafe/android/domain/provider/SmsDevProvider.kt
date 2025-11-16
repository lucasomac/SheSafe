package br.com.lucolimac.shesafe.android.domain.provider

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.usecase.api.SmsDevUseCase
import com.google.firebase.firestore.GeoPoint

class SmsDevProvider(
    private val smsDevUseCase: SmsDevUseCase, private val apiKey: String
) : SmsProvider {
    override suspend fun sendSms(
        contacts: List<SecureContact>,
        message: String,
        location: GeoPoint,
        onSmsStatusUpdate: (Boolean) -> Unit
    ) {
        val body = contacts.map {
            SmsDevBody(
                key = apiKey, msg = message, number = it.phoneNumber.toLong(), type = TYPE_SEND
            )
        }
        smsDevUseCase.sendSms(body = body).collect { response ->
            val isSuccess = response.any { it.situacao == "OK" }
            onSmsStatusUpdate(isSuccess)
        }
    }

    companion object {
        private const val TYPE_SEND = 9
    }
}