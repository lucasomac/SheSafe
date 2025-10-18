package br.com.lucolimac.shesafe.android.data.model

import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName

data class HelpRequestModel(
    @SerializedName("phoneNumber") val phoneNumbers: List<String>,
    @SerializedName("location") val location: GeoPoint,
    @ServerTimestamp @SerializedName("createdAt") val createdAt: Timestamp
) {
    constructor() : this(
        phoneNumbers = listOf(), location = GeoPoint(0.0, 0.0), createdAt = Timestamp.now()
    )

    fun toEntity(): HelpRequest {
        return HelpRequest(phoneNumbers, location, createdAt)
    }

    companion object {
        fun fromEntity(helpRequest: HelpRequest): HelpRequestModel {
            return HelpRequestModel(
                phoneNumbers = helpRequest.phoneNumbers,
                location = helpRequest.location,
                createdAt = helpRequest.createdAt
            )
        }
    }
}