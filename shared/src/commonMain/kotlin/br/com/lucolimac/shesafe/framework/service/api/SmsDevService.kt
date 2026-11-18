package br.com.lucolimac.shesafe.framework.service.api

import br.com.lucolimac.shesafe.data.model.SmsDevItemRequest
import br.com.lucolimac.shesafe.data.model.SmsDevItemResponse
import br.com.lucolimac.shesafe.framework.constants.Endpoints.SMS_DEV_SEND_SMS
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface SmsDevService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(SMS_DEV_SEND_SMS)
    suspend fun sendSms(@Body request: List<SmsDevItemRequest>): List<SmsDevItemResponse>
}
