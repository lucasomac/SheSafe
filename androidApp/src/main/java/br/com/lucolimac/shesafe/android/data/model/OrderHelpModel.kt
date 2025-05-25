package br.com.lucolimac.shesafe.android.data.model

import br.com.lucolimac.shesafe.android.domain.entity.OrderHelp
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class OrderHelpModel(
    @SerializedName("phone") val phone: String,
    @SerializedName("location") val location: LatLng,
    @SerializedName("createdAt") val createdAt: LocalDateTime
) {
    constructor() : this(phone = "", location = LatLng(0.0, 0.0), createdAt = LocalDateTime.now())

    fun toEntity(): OrderHelp {
        return OrderHelp(phone, location, createdAt)
    }

    companion object {
        fun fromEntity(orderHelp: OrderHelp): OrderHelpModel {
            return OrderHelpModel(
                phone = orderHelp.phone,
                location = orderHelp.location,
                createdAt = orderHelp.createdAt
            )
        }
    }
}