package br.com.lucolimac.shesafe.framework.service.api

import br.com.lucolimac.shesafe.data.model.InfoBipRequest
import br.com.lucolimac.shesafe.data.model.InfoBipResponse
import br.com.lucolimac.shesafe.framework.constants.Endpoints.INFO_BIP_SEND_SMS
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

/**
 * Ktorfit HTTP service definition for interacting with the Infobip SMS gateway API.
 */
interface InfoBipService {
    /**
     * Dispatches one or more SMS messages through the Infobip advanced SMS endpoint.
     *
     * @param authorization Value of the HTTP `Authorization` header (e.g., `App <apiKey>`).
     * @param request Serializable [InfoBipRequest] payload containing message bodies and destinations.
     * @return Parsed [InfoBipResponse] reporting bulk and per-message acceptance statuses.
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(INFO_BIP_SEND_SMS)
    suspend fun sendSms(
        @Header("Authorization") authorization: String,
        @Body request: InfoBipRequest,
    ): InfoBipResponse
}
