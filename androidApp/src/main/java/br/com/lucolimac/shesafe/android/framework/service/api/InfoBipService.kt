package br.com.lucolimac.shesafe.android.framework.service.api

import br.com.lucolimac.shesafe.android.data.model.request.InfoBipRequest
import br.com.lucolimac.shesafe.android.data.model.response.InfoBipResponse
import br.com.lucolimac.shesafe.android.framework.constants.Endpoints.INFO_BIP_SEND_SMS
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface InfoBipService {
    @POST(INFO_BIP_SEND_SMS)
    suspend fun sendSms(
        @Header("Authorization") authorization: String,
        @Body request: InfoBipRequest,
    ): Response<InfoBipResponse>
}