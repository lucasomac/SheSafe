package br.com.lucolimac.shesafe.domain.entity

/**
 * Domain entity representing the dispatch result of an individual SMS processed by SMSDev.
 *
 * @property codigo Provider-specific status or result code returned by SMSDev.
 * @property descricao Human-readable status description from the SMSDev gateway.
 * @property id Unique identifier assigned by SMSDev to the queued SMS message.
 * @property situacao High-level dispatch status string reported by SMSDev (e.g., `OK`, `ERRO`).
 */
data class SmsDevEntity(
    val codigo: String, val descricao: String, val id: String, val situacao: String
)
