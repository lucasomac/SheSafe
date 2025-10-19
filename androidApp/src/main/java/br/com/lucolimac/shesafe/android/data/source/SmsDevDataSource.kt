package br.com.lucolimac.shesafe.android.data.source

import br.com.lucolimac.shesafe.android.data.model.request.SmsDevItemRequest
import br.com.lucolimac.shesafe.android.data.model.response.SmsDevItemResponse
import retrofit2.Response

interface SmsDevDataSource {
    suspend fun sendSms(request: List<SmsDevItemRequest>): Response<List<SmsDevItemResponse>>
}