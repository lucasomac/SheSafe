package br.com.lucolimac.shesafe.android.domain.repository

import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity

interface SmsDevRepository {
    suspend fun sendSms(body: List<SmsDevBody>): List<SmsDevEntity>
}