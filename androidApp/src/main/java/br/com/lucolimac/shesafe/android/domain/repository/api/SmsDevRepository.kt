package br.com.lucolimac.shesafe.android.domain.repository.api

import br.com.lucolimac.shesafe.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.domain.entity.SmsDevEntity

interface SmsDevRepository {
    suspend fun sendSms(body: List<SmsDevBody>): List<SmsDevEntity>
}