package br.com.lucolimac.shesafe.android.data.model.request


import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import com.google.gson.annotations.SerializedName

data class SmsDevItemRequest(
    @SerializedName("key") val key: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("number") val number: Long,
    @SerializedName("type") val type: Int
) {
    companion object {
        fun fromEntity(body: SmsDevBody) = SmsDevItemRequest(
            key = body.key, msg = body.msg, number = body.number, type = body.type
        )
    }
}