package br.com.lucolimac.shesafe.domain.entity

data class SmsDevBody(
    val key: String, val msg: String, val number: Long, val type: Int
)
