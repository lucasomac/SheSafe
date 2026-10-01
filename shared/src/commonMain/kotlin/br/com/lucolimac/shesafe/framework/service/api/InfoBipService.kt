package br.com.lucolimac.shesafe.framework.service.api

import br.com.lucolimac.shesafe.data.model.InfoBipRequest
import br.com.lucolimac.shesafe.data.model.InfoBipResponse
import br.com.lucolimac.shesafe.framework.constants.Endpoints.INFO_BIP_SEND_SMS
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST

interface InfoBipService {
    @POST(INFO_BIP_SEND_SMS)
    suspend fun sendSms(
        @Header("Authorization") authorization: String,
        @Body request: InfoBipRequest,
    ): InfoBipResponse
}
