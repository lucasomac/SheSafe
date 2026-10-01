package br.com.lucolimac.shesafe.android.data.source.api

import br.com.lucolimac.shesafe.data.model.SmsDevItemRequest
import br.com.lucolimac.shesafe.data.model.SmsDevItemResponse

interface SmsDevDataSource {
    suspend fun sendSms(request: List<SmsDevItemRequest>): List<SmsDevItemResponse>
}
