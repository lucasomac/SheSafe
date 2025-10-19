package br.com.lucolimac.shesafe.android.framework.service.api

import br.com.lucolimac.shesafe.android.data.model.request.SmsDevItemRequest
import br.com.lucolimac.shesafe.android.data.model.response.SmsDevItemResponse
import br.com.lucolimac.shesafe.android.framework.constants.Endpoints.SMS_DEV_SEND_SMS
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SmsDevService {
    @POST(SMS_DEV_SEND_SMS)
    suspend fun sendSms(@Body request: List<SmsDevItemRequest>): Response<List<SmsDevItemResponse>>
}