package br.com.lucolimac.shesafe.data.model

import br.com.lucolimac.shesafe.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.domain.entity.SmsDevEntity
import kotlinx.serialization.Serializable

@Serializable
data class SmsDevItemRequest(
    val key: String,
    val msg: String,
    val number: Long,
    val type: Int
) {
    companion object {
        fun fromEntity(body: SmsDevBody) = SmsDevItemRequest(
            key = body.key, msg = body.msg, number = body.number, type = body.type
        )
    }
}

@Serializable
data class SmsDevItemResponse(
    val codigo: String,
    val descricao: String,
    val id: String,
    val situacao: String
) {
    fun toEntity() = SmsDevEntity(
        codigo = codigo, descricao = descricao, id = id, situacao = situacao
    )
}
