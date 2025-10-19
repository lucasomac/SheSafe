package br.com.lucolimac.shesafe.android.domain.entity

data class SmsDevBody(
    val key: String, val msg: String, val number: Long, val type: Int
)