package br.com.lucolimac.shesafe.android.data.model

import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class HelpRequestModel(
    @SerializedName("phone") val phone: String,
    @SerializedName("location") val location: LatLng,
    @SerializedName("createdAt") val createdAt: LocalDateTime
) {
    constructor() : this(phone = "", location = LatLng(0.0, 0.0), createdAt = LocalDateTime.now())

    fun toEntity(): HelpRequest {
        return HelpRequest(phone, location, createdAt)
    }

    companion object {
        fun fromEntity(helpRequest: HelpRequest): HelpRequestModel {
            return HelpRequestModel(
                phone = helpRequest.phone,
                location = helpRequest.location,
                createdAt = helpRequest.createdAt
            )
        }
    }
}