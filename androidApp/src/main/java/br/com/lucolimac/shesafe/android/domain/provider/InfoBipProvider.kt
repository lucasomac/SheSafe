package br.com.lucolimac.shesafe.android.domain.provider

import br.com.lucolimac.shesafe.android.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import br.com.lucolimac.shesafe.android.domain.usecase.api.InfoBipUseCase
import com.google.firebase.firestore.GeoPoint

class InfoBipProvider(
    private val infoBipUseCase: InfoBipUseCase, private val apiKey: String
) : SmsProvider {
    override suspend fun sendSms(
        contacts: List<SecureContact>,
        message: String,
        location: GeoPoint,
        onSmsStatusUpdate: (Boolean) -> Unit
    ) {
        val body = InfoBipBody(
            messages = listOf(
                InfoBipBody.Message(
                    content = InfoBipBody.Message.Content(text = message),
                    destinations = contacts.map { contact ->
                        InfoBipBody.Message.Destination(
                            "55${contact.phoneNumber}"
                        )
                    })
            )
        )

        infoBipUseCase.sendSms(body = body, authorization = "App $apiKey").collect { result ->
            val isSuccess = when (result) {
                is SheSafeResult.Success -> true
                else -> false
            }
            onSmsStatusUpdate(isSuccess)
        }
    }
}

