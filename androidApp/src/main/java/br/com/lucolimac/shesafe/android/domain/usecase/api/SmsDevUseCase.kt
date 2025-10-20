package br.com.lucolimac.shesafe.android.domain.usecase.api

import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import kotlinx.coroutines.flow.Flow

interface SmsDevUseCase {
    fun sendSms(body: List<SmsDevBody>): Flow<List<SmsDevEntity>>
}