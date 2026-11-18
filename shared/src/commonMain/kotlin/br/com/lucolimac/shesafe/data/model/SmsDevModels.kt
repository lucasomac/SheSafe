package br.com.lucolimac.shesafe.data.model

import br.com.lucolimac.shesafe.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.domain.entity.SmsDevEntity
import kotlinx.serialization.Serializable

/**
 * Serializable item request payload sent to the SMSDev batch SMS API.
 *
 * @property key API authentication key issued by SMSDev.
 * @property msg Plain-text SMS message body to deliver.
 * @property number Recipient phone number formatted as digits (e.g., country/area code + subscriber number).
 * @property type Numeric SMSDev message route/type code.
 */
@Serializable
data class SmsDevItemRequest(
    val key: String,
    val msg: String,
    val number: Long,
    val type: Int
) {
    /**
     * Factory methods for creating [SmsDevItemRequest] DTOs from domain models.
     */
    companion object {
        /**
         * Converts a domain [SmsDevBody] into a serializable [SmsDevItemRequest].
         *
         * @param body Domain SMSDev request body to map.
         * @return Mapped [SmsDevItemRequest] network DTO.
         */
        fun fromEntity(body: SmsDevBody) = SmsDevItemRequest(
            key = body.key, msg = body.msg, number = body.number, type = body.type
        )
    }
}

/**
 * Serializable per-message response item returned by the SMSDev batch SMS API.
 *
 * @property codigo Provider-specific response or status code string.
 * @property descricao Human-readable status description returned by SMSDev.
 * @property id Unique message dispatch identifier assigned by SMSDev.
 * @property situacao Current queue or delivery status label (e.g., `OK`, `ERRO`).
 */
@Serializable
data class SmsDevItemResponse(
    val codigo: String = "",
    val descricao: String = "",
    val id: String = "",
    val situacao: String = "",
) {
    /**
     * Maps this [SmsDevItemResponse] DTO to its domain [SmsDevEntity] counterpart.
     *
     * @return Corresponding domain [SmsDevEntity] instance.
     */
    fun toEntity() = SmsDevEntity(
        codigo = codigo, descricao = descricao, id = id, situacao = situacao
    )
}
