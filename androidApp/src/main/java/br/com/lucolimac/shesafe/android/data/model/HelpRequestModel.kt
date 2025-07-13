package br.com.lucolimac.shesafe.android.data.model

import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName

data class HelpRequestModel(
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("location") val location: GeoPoint,
    @ServerTimestamp @SerializedName("createdAt") val createdAt: Timestamp
) {

    fun toEntity(): HelpRequest {
        return HelpRequest(phoneNumber, location, createdAt)
    }

    companion object {
        fun fromEntity(helpRequest: HelpRequest): HelpRequestModel {
            return HelpRequestModel(
                phoneNumber = helpRequest.phoneNumber,
                location = helpRequest.location,
                createdAt = helpRequest.createdAt
            )
        }
    }
}