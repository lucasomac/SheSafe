package br.com.lucolimac.shesafe.android.data.model.response


import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import com.google.gson.annotations.SerializedName

data class SmsDevItemResponse(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("id") val id: String,
    @SerializedName("situacao") val situacao: String
) {
    fun toEntity() = SmsDevEntity(
        codigo = codigo, descricao = descricao, id = id, situacao = situacao
    )
}