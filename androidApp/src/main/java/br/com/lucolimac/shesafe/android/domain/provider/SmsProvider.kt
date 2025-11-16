package br.com.lucolimac.shesafe.android.domain.provider

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import com.google.firebase.firestore.GeoPoint

interface SmsProvider {
    suspend fun sendSms(
        contacts: List<SecureContact>,
        message: String,
        location: GeoPoint,
        onSmsStatusUpdate: (Boolean) -> Unit
    )
}
