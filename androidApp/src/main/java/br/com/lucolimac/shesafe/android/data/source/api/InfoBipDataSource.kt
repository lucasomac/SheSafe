package br.com.lucolimac.shesafe.android.data.source.api

import br.com.lucolimac.shesafe.android.data.model.request.InfoBipRequest
import br.com.lucolimac.shesafe.android.data.model.response.InfoBipResponse
import retrofit2.Response

interface InfoBipDataSource {
    suspend fun sendSms(
        request: InfoBipRequest,
        authorization: String
    ): Response<InfoBipResponse>
}