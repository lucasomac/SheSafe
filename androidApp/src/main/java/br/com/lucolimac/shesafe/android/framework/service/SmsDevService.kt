package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.request.SmsDevItemRequest
import br.com.lucolimac.shesafe.android.data.model.response.SmsDevItemResponse
import retrofit2.Response
import br.com.lucolimac.shesafe.android.framework.constants.Endpoints.SEND_SMS_ENDPOINT
import retrofit2.http.Body
import retrofit2.http.POST

interface SmsDevService {
    @POST(SEND_SMS_ENDPOINT)
    suspend fun sendSms(@Body request: List<SmsDevItemRequest>): Response<List<SmsDevItemResponse>>
}