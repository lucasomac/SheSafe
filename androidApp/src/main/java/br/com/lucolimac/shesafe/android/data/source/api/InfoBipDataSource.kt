package br.com.lucolimac.shesafe.android.data.source.api

import br.com.lucolimac.shesafe.data.model.InfoBipRequest
import br.com.lucolimac.shesafe.data.model.InfoBipResponse

interface InfoBipDataSource {
    suspend fun sendSms(
        request: InfoBipRequest,
        authorization: String
    ): InfoBipResponse
}
