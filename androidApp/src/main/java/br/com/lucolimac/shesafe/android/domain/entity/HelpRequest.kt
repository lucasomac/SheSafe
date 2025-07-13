package br.com.lucolimac.shesafe.android.domain.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class HelpRequest(
    val phoneNumber: String, val location: GeoPoint, val createdAt: Timestamp
) {
    val linkMap =
        "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
}
