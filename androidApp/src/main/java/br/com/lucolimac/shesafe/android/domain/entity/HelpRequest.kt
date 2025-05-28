package br.com.lucolimac.shesafe.android.domain.entity

import java.time.LocalDateTime
import com.google.android.gms.maps.model.LatLng

data class HelpRequest(
    val phoneNumber: String, val location: LatLng, val createdAt: LocalDateTime
) {
    val linkMap =
        "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
}
