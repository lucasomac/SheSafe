package br.com.lucolimac.shesafe.domain.entity

/**
 * Domain model representing a single outbound SMS request item for the SMSDev gateway.
 *
 * @property key API authentication key issued by SMSDev.
 * @property msg Plain-text SMS body to transmit.
 * @property number Numeric recipient phone number (digits only).
 * @property type SMSDev message type/route code (e.g., `9` for standard transactional SMS).
 */
data class SmsDevBody(
    val key: String, val msg: String, val number: Long, val type: Int
)
